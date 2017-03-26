import java.io.*;
import java.net.*;

public class server {
	
	private static int REQ_CODE = 0;
	
	public static void main(String args[]) {
		
		try {
			
			int req_code = 0;
			
			// argument retrieving and checking
			if (args.length == 1) {
				REQ_CODE = Integer.parseInt(args[0]);
			} else {
				System.out.println("command usage: java server <req_code>");
				return;
			}
			
			ServerSocket serverSocket = new ServerSocket(0);
			int n_port = serverSocket.getLocalPort();

			System.out.println("SERVER_PORT="+n_port);
			
			// run server
			while (true) {
				
				Socket connectionSocket = serverSocket.accept();
				
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				
				req_code = Integer.parseInt(inFromClient.readLine());
				
				// check request code, if valid perform transaction otherwise close connection
				if (req_code == REQ_CODE) transact(connectionSocket);
				else connectionSocket.close();
			}
			
		} catch (NumberFormatException nfe) {
			System.out.println("req_code is not of type integer");
		} catch (UnknownHostException uhe) {
			System.out.println("Unknown host: server name could not be resolved");
		} catch (SocketException se) {
			System.out.println("Invalid server address or port number: server socket could not open");
		} catch (IOException ioe) {
			System.out.println("I/O Exception occured");
		} catch (Exception e) {
			System.out.println("Exception occured");
			e.printStackTrace();
		}
	}
	
	/* Transaction stage */
	private static void transact(Socket connectionSocket) throws SocketException, IOException {
		
		DatagramSocket transSocket = new DatagramSocket();
		int r_port = transSocket.getLocalPort();
		
		DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
		
		outToClient.writeBytes(r_port+"\n");
		
		connectionSocket.close();
		
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
			
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		transSocket.receive(receivePacket);
		
		String message = new String(receivePacket.getData());
		
		InetAddress IPAddr = receivePacket.getAddress();
		int port = receivePacket.getPort();
		
		message = reverse(message);
		
		sendData = message.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddr, port);
		transSocket.send(sendPacket);
		transSocket.close();
	}
	
	// helper function to reverse a string
	private static String reverse(String msg) {
		String newMsg = "";
		
		for(int i = msg.length()-1; i >= 0; i--) {
			newMsg += msg.charAt(i);
		}
		
		return newMsg;
	}
}
