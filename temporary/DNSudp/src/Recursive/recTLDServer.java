package Recursive;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class recTLDServer {
	public static int PORT = 3805;
	public static String IPaddress = "localhost";
	
	public static HashMap<String, String> dnsRecords = new HashMap<>();
	
	public static void main(String[] args) {
		dnsRecords.put("apple.ac.bd", "17.253.144.10");
		dnsRecords.put("microsoft.ac.bd", "40.76.4.15");
		dnsRecords.put("netflix.ac.bd", "45.57.151.12");
		dnsRecords.put("instagram.ac.bd", "52.0.76.128");
		dnsRecords.put("ebay.ac.bd", "66.211.181.161");
		dnsRecords.put("paypal.ac.bd", "64.4.250.39");
		dnsRecords.put("spotify.ac.bd", "35.186.224.25");
		dnsRecords.put("wordpress.ac.bd", "192.0.78.9");
		dnsRecords.put("bbc.ac.bd", "151.101.64.81");
		dnsRecords.put("cnn.ac.bd", "151.101.1.67");
		
		
		try {
			DatagramSocket serverSocket = new DatagramSocket(PORT);
			System.out.println("DNS Server Established");
			
			while (true) {
				byte[] receiveData = new byte[1024];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				
				String domainName = new String(receivePacket.getData(), 0, receivePacket.getLength());
				InetAddress clientAddress = receivePacket.getAddress();
				int clientPort = receivePacket.getPort();
				
				System.out.println("Received DNS request for: " + domainName);
				
				String ipAddress = handleDNSRequest(serverSocket,domainName);
				byte[] sendData = ipAddress.getBytes();
				
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
				serverSocket.send(sendPacket);
				
				System.out.println("Sent DNS response: " + ipAddress);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String handleDNSRequest( DatagramSocket serverSocket, String domainName) throws IOException {
		if ( dnsRecords.containsKey(domainName) ) {
			return dnsRecords.get(domainName);
		} else {
			//sent to root server
			byte[] sendData = domainName.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(recAuthServer.IPaddress), recAuthServer.PORT);
			serverSocket.send(sendPacket);
			
			System.out.println("Sent DNS response to Auth server");
			
			byte[] receiveData = new byte[ 1024 ];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			
			String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
			
			System.out.println("Received DNS response from Auth server :" + message);
			System.out.println(receivePacket.getPort());
			
			
			return message;
			
			
		}
	}
}
