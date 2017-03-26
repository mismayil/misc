import java.io.*;
import java.net.*;
import java.util.*;

public class receiver {
	
	private static final int PKT_SIZE = 512;
	private static final int MAX_SEQ_NUM = 32;
	private static final int ACK = 0;
	private static final int DATA = 1;
	private static final int EOT = 2;
	
	private static InetAddress nemulatorIPAddress;
	private static String nemulatorAddress;
	private static int nemulatorPort;
	private static int receiverPort;
	private static String fileName;
	
	private static DatagramSocket receiverSocket;
	private static int expectedSeqNum = 0;
	
	private static final String arrivalLog = "arrival.log";
	private static boolean firstPktReceived = false;
	
	public static void main(String[] args) {
		
		// get arguments
		nemulatorAddress = args[0];
		nemulatorPort = Integer.parseInt(args[1]);
		receiverPort = Integer.parseInt(args[2]);
		fileName = args[3];
		
		try {
			
			nemulatorIPAddress = InetAddress.getByName(nemulatorAddress);
			
			receiverSocket = new DatagramSocket(receiverPort);
			
			FileWriter receiverFile = new FileWriter(fileName);
			FileWriter arrivalLogFile = new FileWriter(arrivalLog);
			
			byte[] receiveData = new byte[PKT_SIZE];
			byte[] sendData = new byte[PKT_SIZE];
			
			DatagramPacket sendPacket, receivePacket;
			packet pkt;
			
			while (true) {
				
				receivePacket = new DatagramPacket(receiveData, receiveData.length);
				receiverSocket.receive(receivePacket);
				
				pkt = packet.parseUDPdata(receivePacket.getData());
				arrivalLogFile.write(pkt.getSeqNum() + "\n");

				if (pkt.getType() == DATA) {
					
					if (pkt.getSeqNum() == expectedSeqNum) {
						
						if (pkt.getSeqNum() == 0) firstPktReceived = true;
						
						receiverFile.write(new String(pkt.getData()));

						// send new ack
						pkt = packet.createACK(pkt.getSeqNum());
						sendData = pkt.getUDPdata();
						sendPacket = new DatagramPacket(sendData, sendData.length, nemulatorIPAddress, nemulatorPort);
						receiverSocket.send(sendPacket);

						expectedSeqNum = (++expectedSeqNum) % MAX_SEQ_NUM;

					} else {
						
						// send most recent ack
						if (firstPktReceived) {

							pkt = packet.createACK(expectedSeqNum-1);
							sendData = pkt.getUDPdata();
							sendPacket = new DatagramPacket(sendData, sendData.length, nemulatorIPAddress, nemulatorPort);
							receiverSocket.send(sendPacket);

						}
					}
				} else if (pkt.getType() == EOT) {
                    
                    // send end of tranmission packet                   
					pkt = packet.createEOT(expectedSeqNum);
					sendData = pkt.getUDPdata();
					sendPacket = new DatagramPacket(sendData, sendData.length, nemulatorIPAddress, nemulatorPort);
					receiverSocket.send(sendPacket);
					break;
				}
			}
			
			receiverSocket.close();
			receiverFile.close();
			arrivalLogFile.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
