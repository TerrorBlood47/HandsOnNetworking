package Recursive;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class recAuthServer {
	public static int PORT = 9805;
	public static String IPaddress = "localhost";
	
	public static HashMap<String, String> dnsRecords = new HashMap<>();
	
	public static void main(String[] args) {
		dnsRecords.put("yahoo.ac.bd", "98.137.246.7");
		dnsRecords.put("bing.ac.bd", "204.79.197.200");
		dnsRecords.put("amazon.ac.bd", "176.32.103.205");
		dnsRecords.put("facebook.ac.bd", "31.13.65.36");
		dnsRecords.put("twitter.ac.bd", "104.244.42.129");
		dnsRecords.put("stackoverflow.ac.bd", "151.101.1.69");
		dnsRecords.put("github.ac.bd", "140.82.121.4");
		dnsRecords.put("wikipedia.ac.bd", "91.198.174.192");
		dnsRecords.put("reddit.ac.bd", "151.101.1.140");
		dnsRecords.put("linkedin.ac.bd", "108.174.10.10");
		
		
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
	
	private static String handleDNSRequest( DatagramSocket serverSocket, String domainName) {
		return dnsRecords.getOrDefault(domainName, "not found");
	}
}
