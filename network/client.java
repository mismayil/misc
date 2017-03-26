import java.io.*;
import java.net.*;
import java.security.InvalidParameterException;

public class client {
	
	public static void main(String args[]) {
		
		String serverAddr = "";
		int n_port = 0;
		int req_code = 0;
		String message = "";
		int r_port = 0;
		
		try {
			
			// argument retrieving and checking
			if (args.length == 4) {
				
				serverAddr = args[0];
				n_port = Integer.parseInt(args[1]);
				req_code = Integer.parseInt(args[2]);
				message = args[3];
				
				if (n_port < 0) {
					System.out.println("n_port should be positive integer");
					return;
				}
				
			} else {
				System.out.println("command usage: java client <server_address> <n_port> <req_code> message");
				return;
			}
			
			r_port = negotiate(serverAddr, n_port, req_code);

			String reverseMsg = transact(serverAddr, r_port, message);
			
			System.out.println(reverseMsg);
			
			
		} catch (NumberFormatException nfe) {
			System.out.println("n_port or req_code is not of type integer");
		}  catch (InvalidParameterException ipe) {
			System.out.println(ipe.getMessage());
		} catch (UnknownHostException uhe) {
			System.out.println("Unknown host: server name could not be resolved");
		} catch (SocketException se) {
			System.out.println("Invalid server address or port number: client socket could not open");
		} catch (IOException ioe) {
			System.out.println("I/O exception occured");
		} catch (Exception e) {
			System.out.println("Exception occured");
			e.printStackTrace();
		}
	}

	/* Negotiation stage */
	private static int negotiate(String serverAddr, int n_port, int req_code) throws UnknownHostException, IOException, InvalidParameterException {
			
	    int r_port = 0;

		Socket clientSocket = new Socket(serverAddr, n_port);
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.writeBytes(req_code + "\n");
		
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String input = inFromServer.readLine();
		
		clientSocket.close();
		
		if (input != null) r_port = Integer.parseInt(input);
		else throw new InvalidParameterException("Invalid req_code: connection failed");

		return r_port;
	}
	
	/* Transaction stage */
	private static String transact(String serverAddr, int r_port, String message) throws SocketException, UnknownHostException, IOException {
		
		DatagramSocket transSocket = new DatagramSocket();
		
		InetAddress IPAddr = InetAddress.getByName(serverAddr);
		
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		
		sendData = message.getBytes();
		
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddr, r_port);
		transSocket.send(sendPacket);
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		transSocket.receive(receivePacket);
		
		String reverseMsg = new String(receivePacket.getData());
		
		transSocket.close();
		
		return reverseMsg;

	}
}
