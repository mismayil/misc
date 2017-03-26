import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class Router {
	
	public static final int NBR_ROUTER = 5;
	public static final int PKT_SIZE = 100;
	public static final int INF = Integer.MAX_VALUE;
	public static final int TIMEOUT = 30000;
	
	private ArrayList<RIBEntry> RIB = new ArrayList<RIBEntry>(NBR_ROUTER);
	private ArrayList<TopoEntry> TOPODB = new ArrayList<TopoEntry>(NBR_ROUTER);
	private int[][] TOPOGRAPH = new int[NBR_ROUTER][NBR_ROUTER];
	private int routerID;
	private int routerPort;
	private FileWriter routerFile;
	
	public Router(int routerID, int routerPort) throws IOException {
		this.routerID = routerID;
		this.routerPort = routerPort;
		this.routerFile = new FileWriter(new File("router" + routerID + ".log"));
		
		RIB.add(new RIBEntry(routerID, routerID, 0, false, 0));
		
		for(int i = 0; i < NBR_ROUTER; i++) {
			for(int j = 0; j < NBR_ROUTER; j++) {
				TOPOGRAPH[i][j] = INF;
			}
		}
		
		TOPOGRAPH[0][0] = 0;
	}
	
	public static void main(String[] args) {
		
		try {
			
			if (args.length != 4) {
				System.out.println("command usage: java Router <router_id> <nse_host> <nse_port> <router_port>");
				return;
			}

			int id = Integer.parseInt(args[0]);
			String nseHost = args[1];
			int nsePort = Integer.parseInt(args[2]);
			int port = Integer.parseInt(args[3]);
			
			Router router = new Router(id, port);
			
			router.run(nseHost, nsePort);
			
		} catch (IOException io) {
			System.out.println("IO Exception: router file could not open");
		} catch (NumberFormatException nfe) {
			System.out.println("id, nse port or router port is not of type integer");
		}
	}
	
	public void run(String nseHost, int nsePort) {
		
		try {
			
			InetAddress nseIPAddress = InetAddress.getByName(nseHost);
			
			DatagramSocket routerSocket = new DatagramSocket(routerPort);
			
			byte[] sendData, receiveData;
			DatagramPacket sendPacket, receivePacket;
			
			// send an INIT packet to nse
			PKT_INIT pktInit = new PKT_INIT(routerID);
			sendData = pktInit.getUDPdata();
			sendPacket = new DatagramPacket(sendData, sendData.length, nseIPAddress, nsePort);
			routerSocket.send(sendPacket);
			
			routerFile.write("R"+routerID+" sends an INIT: router_id "+routerID+"\n");
			
			// receive a circuit db from nse
			receiveData = new byte[PKT_SIZE];
			receivePacket = new DatagramPacket(receiveData, receiveData.length);
			routerSocket.receive(receivePacket);

			// set timeout
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					logDB();
					
					try {
						routerFile.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

					System.exit(0);
				}
			}, TIMEOUT);

			CIRCUIT_DB circuitDB = CIRCUIT_DB.parseUDPdata(receivePacket.getData());
			
			routerFile.write("R"+routerID+" receives a CIRCUIT_DB: nbr_link "+circuitDB.nbrLink+"\n");
			
			TopoEntry topoEntry = new TopoEntry(routerID, circuitDB);
			TOPODB.add(topoEntry);
			
			logDB();
			
			// send a HELLO packet to each attached link
			ArrayList<LINK_COST> neighbors = circuitDB.linkCost;
			
			for(int i = 0; i < neighbors.size(); i++) {
				PKT_HELLO pktHello = new PKT_HELLO(routerID, neighbors.get(i).linkID);
				sendData = pktHello.getUDPdata();
				sendPacket = new DatagramPacket(sendData, sendData.length, nseIPAddress, nsePort);
				routerSocket.send(sendPacket);

				routerFile.write("R"+routerID+" sends a HELLO: router_id "+routerID+" link_id "+neighbors.get(i).linkID+"\n");
			}
			
			// receive a HELLO PDU or LSPDU from a neighbor
			while(true) {
				
				receiveData = new byte[PKT_SIZE];
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				routerSocket.receive(receivePacket);
				
				byte[] data = receivePacket.getData();
				
				// check if HELLO packet or LSPDU packet
				if (data[16] == 0) {
					
					// received HELLO packet
					PKT_HELLO pktHello = PKT_HELLO.parseUDPdata(data);
					int neighborID = pktHello.routerID;
					int neighborLink = pktHello.linkID;
					
					routerFile.write("R"+routerID+" receives a HELLO: router_id "+neighborID+" link_id "+neighborLink+"\n");
					
					RIB.add(new RIBEntry(neighborID, INF, INF, true, neighborLink));
					
					logDB();

					for(int i = 0; i < TOPODB.size(); i++) {
						TopoEntry entry = TOPODB.get(i);
						
						ArrayList<LINK_COST> links = entry.circuitDB.linkCost;
						
						for(int j = 0; j < links.size(); j++) {
							PKT_LSPDU pktLSPDU = new PKT_LSPDU(routerID, entry.routerID, links.get(j).linkID, links.get(j).cost, neighborLink);
							sendData = pktLSPDU.getUDPdata();
							sendPacket = new DatagramPacket(sendData, sendData.length, nseIPAddress, nsePort);
							routerSocket.send(sendPacket);
							
							routerFile.write("R"+routerID+" sends an LS PDU:"+
												" sender "+pktLSPDU.sender+
												" router_id "+pktLSPDU.routerID+
												"link_id "+pktLSPDU.linkID+
												" cost "+pktLSPDU.cost+
												" via "+pktLSPDU.via+"\n");
						}
					}
					
				} else {

					// received LSPDU packet
					PKT_LSPDU pktLSPDU = PKT_LSPDU.parseUDPdata(data);
					int remoteID = pktLSPDU.routerID;
					
					routerFile.write("R"+routerID+" receives an LS PDU:"+
							" sender "+pktLSPDU.sender+
							" router_id "+pktLSPDU.routerID+
							" link_id "+pktLSPDU.linkID+
							" cost "+pktLSPDU.cost+
							" via "+pktLSPDU.via+"\n");
					
					circuitDB = findID(TOPODB, remoteID);
					
					boolean dbUpdated = false;

					if (circuitDB == null) {
						
						// doesnt exist, add new one
						circuitDB = new CIRCUIT_DB();
						circuitDB.addLink(new LINK_COST(pktLSPDU.linkID, pktLSPDU.cost));
						TOPODB.add(new TopoEntry(remoteID, circuitDB));
						logDB();
						dbUpdated = true;
						
					} else {
						
						// exists and add link if not duplicate
						if (!containsLink(circuitDB.linkCost, pktLSPDU.linkID, pktLSPDU.cost)) {
							circuitDB.addLink(new LINK_COST(pktLSPDU.linkID, pktLSPDU.cost));
							logDB();
							dbUpdated = true;
						}
					}
										
					// send the LSPDU to neighbors
					if (dbUpdated) {
						for(int i = 0; i < RIB.size(); i++) {
							RIBEntry ribEntry = RIB.get(i);
							
							if (ribEntry.destID != remoteID && ribEntry.isNeighbor) {
								pktLSPDU.sender = routerID;
								pktLSPDU.via = ribEntry.linkID;
								sendData = pktLSPDU.getUDPdata();
								sendPacket = new DatagramPacket(sendData, sendData.length, nseIPAddress, nsePort);
								routerSocket.send(sendPacket);

								routerFile.write("R"+routerID+" sends an LS PDU:"+
										" sender "+pktLSPDU.sender+
										" router_id "+pktLSPDU.routerID+
										" link_id "+pktLSPDU.linkID+
										" cost "+pktLSPDU.cost+
										" via "+pktLSPDU.via+"\n");
							}
						}
						
						// update topology graph
						updateGraph(remoteID, pktLSPDU.linkID, pktLSPDU.cost);

						// run Shortest Path Algorithm on TOPOGRAPH and update RIB
						updateRIB();

						logDB();
					}
				}
			}
			
		} catch (UnknownHostException uhe) {
			System.out.println("Unknown host: Network State Emulator name could not be resolved");
		} catch (SocketException se) {
			System.out.println("Socket exception: router socket could not open");
		} catch (IOException ioe) {
			System.out.println("IO Exception occured");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	// log TOPODB and RIB to router file
	private void logDB() {
		
		try {
			
			// log TOPODB
			routerFile.write("# Topology database \n");
			for(int i = 0; i < TOPODB.size(); i++) {
				TopoEntry entry = TOPODB.get(i);
				CIRCUIT_DB circuit = entry.circuitDB;
				
				routerFile.write("R"+routerID+" -> R"+entry.routerID+" nbr link "+circuit.nbrLink+"\n");
				
				for(int j = 0; j < circuit.nbrLink; j++) {
					LINK_COST link = circuit.linkCost.get(j);
					routerFile.write("R"+routerID+" -> R"+entry.routerID+" link "+link.linkID+" cost "+link.cost+"\n");
				}
			}
			
			// log RIB
			routerFile.write("# RIB \n");
			for(int i = 0; i < RIB.size(); i++) {
				RIBEntry entry = RIB.get(i);
				String via = (entry.viaID == INF) ? "INF": "R" + entry.viaID;
				String cost = entry.cost == INF ? "INF" : "" + entry.cost;
				if (entry.destID == routerID) routerFile.write("R"+routerID+" -> R"+routerID+" -> Local, 0 \n");
				else routerFile.write("R"+routerID+" -> R"+entry.destID+" -> "+via+", "+cost+"\n");
			}
			
		} catch (IOException io) {
			System.out.println("IO Exception: write to file failed");
		}

	}
	
	// helper function to find topodb entry
	private CIRCUIT_DB findID(ArrayList<TopoEntry> topodb, int id) {
		
		for(int i = 0; i < topodb.size(); i++) {
			if (topodb.get(i).routerID == id) return topodb.get(i).circuitDB;
		}
		
		return null;
		
	}
	
	//helper function to check if this link is duplicate
	private boolean containsLink(ArrayList<LINK_COST> links, int link, int cost) {
		
		for(int i = 0; i < links.size(); i++) {
			if (links.get(i).linkID == link && links.get(i).cost == cost) return true;
		}
		
		return false;
	}
	
	// helper function to update TOPOGRAPH
	private void updateGraph(int ID, int link, int cost) {
		int n = TOPODB.size();
		int index = 0;
		
		for(int i = 0; i < n; i++) {
			if (TOPODB.get(i).routerID == ID) {
				index = i;
				break;
			}
		}
		
		boolean linkFound = false;
		
		for(int i = 0; i < n; i++) {
			TopoEntry entry = TOPODB.get(i);
			
			if (i != index) {
				ArrayList<LINK_COST> links = entry.circuitDB.linkCost;
				
				for(int j = 0; j < links.size(); j++) {
					if (links.get(j).linkID == link) {
						TOPOGRAPH[i][index] = cost;
						TOPOGRAPH[index][i] = cost;
						linkFound = true;
						break;
					}
				}
			}
			
			if (linkFound) break;
		}
	}
	
	// helper function to run SPF on TOPODB and update RIB 
	private void updateRIB() {
		int n = TOPODB.size();
		int srcIndex = 0;
		
		int[] d = new int[n]; // distance vector
		int[] p = new int[n]; // predecessor vector
	 
		int[] V = new int[n]; // vertex vector
		
		srcIndex = initialize(V, d, p, n);
		
		int[] N = V.clone(); // temp vertex vector
		int numv = 0;
		
		while (numv != n) {
			int u = findMinVertex(N, d);
			N[u] = INF;
			
			for(int v = 0; v < n; v++) {
				if (TOPOGRAPH[u][v] != INF) relax(d, p, u, v);
			}
			
			numv++;
		}
		
		// update RIB
		for(int i = 0; i < d.length; i++) {
			int destID = V[i];
			int cost = d[i];
			int viaID = V[i];
			
			int j = i;
			
			// determine via router
			while (p[j] != srcIndex) {
				if (p[j] != INF) {
					viaID = V[p[j]];
					j = p[j];
				} else {
					viaID = INF;
					break;
				}
			}
			
			boolean entryFound = false;
			
			for(int k = 0; k < RIB.size(); k++) {
				RIBEntry entry = RIB.get(k);
				
				if (entry.destID == destID) {
					entry.viaID = viaID;
					entry.cost = cost;
					entryFound = true;
					break;
				}
			}
			
			if(!entryFound) {
				RIB.add(new RIBEntry(destID, viaID, cost, false, INF));
			}	
		}
	}
	
	// helper function to initialize graph for SPF
	private int initialize(int[] V, int[] d, int[] p, int n) {
		int index = 0;
		
		for(int i = 0; i < n; i++) {
			V[i] = TOPODB.get(i).routerID;
			if (V[i] == routerID) index = i;
		}
		
		for(int i = 0; i < n; i++) {
			d[i] = INF;
			p[i] = INF;
		}
		
		d[index] = 0;
		p[index] = 0;
		
		return index;
	}
	
	// helper function to relax graph edges
	private void relax(int[] d, int[] p, int u, int v) {
		
		int t = d[u] + TOPOGRAPH[u][v];
		
		if (d[v] > t) {
			d[v] = t;
			p[v] = u;
		}
	}
	
	// helper function to find min weight vertex from source in a graph
	private int findMinVertex(int[] V, int[] d) {
		
		int min = INF;
		int index = 0;
		
		for(int i = 0; i < V.length; i++) {
			if (V[i] != INF && d[i] < min) {
				min = d[i];
				index = i;
			}
		}
		
		return index;
	}

}

/* CLASS DEFINITIONS */

class PKT_HELLO {
	int routerID;
	int linkID;
	
	PKT_HELLO(int routerID, int linkID) {
		this.routerID = routerID;
		this.linkID = linkID;
	}
	
	static PKT_HELLO parseUDPdata(byte[] UDPdata) throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(UDPdata);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int routerID = buffer.getInt();
		int linkID = buffer.getInt();
		return new PKT_HELLO(routerID, linkID);
	}
	
	byte[] getUDPdata() {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(routerID);
        buffer.putInt(linkID);
		return buffer.array();
	}
}

class PKT_LSPDU {
    int sender;
	int routerID;
	int linkID;
	int cost;
	int via;
	
	PKT_LSPDU(int sender, int routerID, int linkID, int cost, int via) {
		this.sender = sender;
		this.routerID = routerID;
		this.linkID = linkID;
		this.cost = cost;
		this.via = via;
	}
	
	static PKT_LSPDU parseUDPdata(byte[] UDPdata) throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(UDPdata);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int sender = buffer.getInt();
		int routerID = buffer.getInt();
		int linkID = buffer.getInt();
		int cost = buffer.getInt();
		int via = buffer.getInt();
		return new PKT_LSPDU(sender, routerID, linkID, cost, via);
	}
	
	byte[] getUDPdata() {
		ByteBuffer buffer = ByteBuffer.allocate(20);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(sender);
		buffer.putInt(routerID);
        buffer.putInt(linkID);
        buffer.putInt(cost);
        buffer.putInt(via);
		return buffer.array();
	}
}

class PKT_INIT {
	int routerID;
	
	PKT_INIT(int routerID) {
		this.routerID = routerID;
	}
	
	byte[] getUDPdata() {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(routerID);
		return buffer.array();
	}
}

class LINK_COST {
	int linkID;
	int cost;
	
	LINK_COST(int linkID, int cost) {
		this.linkID = linkID;
		this.cost = cost;
	}
}

class CIRCUIT_DB {
	int nbrLink;
	ArrayList<LINK_COST> linkCost;
	
	CIRCUIT_DB() {
		this.nbrLink = 0;
		this.linkCost = new ArrayList<LINK_COST>();
	}
	
	public void addLink(LINK_COST link) {
		this.linkCost.add(link);
		this.nbrLink++;
	}
	
	static CIRCUIT_DB parseUDPdata(byte[] UDPdata) throws Exception {
		ByteBuffer buffer = ByteBuffer.wrap(UDPdata);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		int nbrLink = buffer.getInt();
		int linkID, cost;
		CIRCUIT_DB cdb = new CIRCUIT_DB();
		
		for (int i = 0; i < nbrLink; i++) {
			linkID = buffer.getInt();
			cost = buffer.getInt();
			cdb.addLink(new LINK_COST(linkID, cost));
		}
		
		return cdb;
	}
}

class RIBEntry {
	int destID;
	int viaID;
	int cost;
	boolean isNeighbor = false;
	int linkID;
	
	RIBEntry(int destID, int viaID, int cost, boolean neighbor, int linkID) {
		this.destID = destID;
		this.viaID = viaID;
		this.cost = cost;
		this.isNeighbor = neighbor;
		this.linkID = linkID;
	}
}

class TopoEntry {
	int routerID;
	CIRCUIT_DB circuitDB;
	
	TopoEntry(int routerID, CIRCUIT_DB circuitDB) {
		this.routerID = routerID;
		this.circuitDB = circuitDB;
	}
}