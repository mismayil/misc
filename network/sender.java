import java.io.*;
import java.net.*;
import java.util.*;

public class sender {
	
	private static final int WINDOW_SIZE = 10;
	private static final int ACK = 0;
	private static final int DATA = 1;
	private static final int EOT = 2;
	private static final int PKT_SIZE = 512;
	private static final int DATA_SIZE = 500;
	private static final int TIMEOUT = 500; //in ms
	private static final int MAX_SEQ_NUM = 32;
	
	private static int sendBase = 0;
	private static int nextSeqNum = 0;
	private static Timer timer = new Timer();
	
	private static InetAddress nemulatorIPAddress;
	private static String nemulatorAddress;
	private static int nemulatorPort;
	private static int senderPort;
	private static String fileName;
	
	private static packet[] pkts = new packet[MAX_SEQ_NUM];
	private static int acks;
	private static DatagramSocket senderSocket;
	
	private static String seqnumLog = "seqnum.log";
	private static String ackLog = "ack.log";
	private static FileWriter seqnumLogFile;
	private static FileWriter ackLogFile;
	
	public static void main(String[] args) {
		
		// get arguments
		nemulatorAddress = args[0];
		nemulatorPort = Integer.parseInt(args[1]);
		senderPort = Integer.parseInt(args[2]);
		fileName = args[3];
		
		try {
			
			nemulatorIPAddress = InetAddress.getByName(nemulatorAddress);
			
			senderSocket = new DatagramSocket(senderPort);
			
			byte[] receiveData = new byte[PKT_SIZE];
			byte[] sendData = new byte[PKT_SIZE];
			DatagramPacket sendPacket, receivePacket;
			
			File file = new File(fileName);
			Scanner fin = new Scanner(file);
			
			Queue<byte[]> dataQueue = new LinkedList<byte[]>();
			
			// read the file and store it in queue
			String contents = "";
			byte[] dataBytes = null;
			int byteNum = 0;
			packet pkt = null;
			byte[] data = null;
			
			fin.useDelimiter("\n");
			
			while (fin.hasNextLine()) {
				contents += fin.nextLine() + "\n";
			}
			
			dataBytes = contents.getBytes();
			
			for(int i = 0; i < dataBytes.length; i = i + 500) {
				
				data = new byte[DATA_SIZE];
				int k = 0;
				int end = Math.min(i+DATA_SIZE, dataBytes.length);
				
				for(int j = i; j < end; j++) {
					data[k++] = dataBytes[j];
				}
				
				dataQueue.add(data);
			}
			
			fin.close();
			/* reading file completed */

			seqnumLogFile = new FileWriter(new File(seqnumLog));
			ackLogFile = new FileWriter(new File(ackLog));
			
			acks = dataQueue.size();
			
			while(true) {
				
				if (acks > 0) {
					
					// if window is not full, send packets
					for(int i = nextSeqNum; i < sendBase + WINDOW_SIZE; i++) {
						
						if (!dataQueue.isEmpty()) {
							
							data = dataQueue.poll();
							pkt = packet.createPacket(i, new String(data));
							pkts[i%MAX_SEQ_NUM] = pkt;
							sendData = pkt.getUDPdata();
							sendPacket = new DatagramPacket(sendData, sendData.length, nemulatorIPAddress, nemulatorPort);
							senderSocket.send(sendPacket);
							
							seqnumLogFile.write(pkt.getSeqNum() + "\n");
							
							if (sendBase == nextSeqNum) {
								timer.cancel();
								timer = new Timer();
								startTimer();
							}
							
							nextSeqNum = (++nextSeqNum) % MAX_SEQ_NUM;
							
						} else break;
					}
					
					receivePacket = new DatagramPacket(receiveData, receiveData.length);
					senderSocket.receive(receivePacket);
					
					pkt = packet.parseUDPdata(receivePacket.getData());

					// get acknowledgements
					if (pkt.getType() == ACK) {
						
						if ((sendBase <= pkt.getSeqNum() && pkt.getSeqNum() < nextSeqNum) ||
                            (nextSeqNum < sendBase && pkt.getSeqNum() < nextSeqNum)) {

							if (sendBase <= pkt.getSeqNum()) {
								acks -= pkt.getSeqNum() - sendBase + 1;
							} else {
								acks -= MAX_SEQ_NUM - sendBase + pkt.getSeqNum() + 1;
							}
							
							sendBase = (pkt.getSeqNum() + 1) % MAX_SEQ_NUM;

							timer.cancel();

						    if (sendBase != nextSeqNum) {
							    timer = new Timer();
								startTimer();
							}
						}
						
						ackLogFile.write(pkt.getSeqNum() + "\n");
					}	
					
				} else {
					timer.cancel();
					break;
				}
			}
			
			// send end of transmission packet
			pkt = packet.createEOT(nextSeqNum);
			sendData = pkt.getUDPdata();
			sendPacket = new DatagramPacket(sendData, sendData.length, nemulatorIPAddress, nemulatorPort);
			senderSocket.send(sendPacket);
            
            senderSocket.close();
			seqnumLogFile.close();
			ackLogFile.close();
			
			timer.purge();
			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// helper to start timer
	private static void startTimer() {
		
		timer.schedule(new TimerTask() {
			
			public void run() {	
                 
                 timer = new Timer();
				 startTimer();
				 
				 int endBase = nextSeqNum;
				 
				 if (sendBase > nextSeqNum) endBase += MAX_SEQ_NUM;
				 
                 for(int i = sendBase; i < endBase; i++) {
					
					try {
						
						byte[] sendData = pkts[i%MAX_SEQ_NUM].getUDPdata();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, nemulatorIPAddress, nemulatorPort);
						senderSocket.send(sendPacket);
						
						seqnumLogFile.write(pkts[i%MAX_SEQ_NUM].getSeqNum() + "\n");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}, TIMEOUT);
	}
}
