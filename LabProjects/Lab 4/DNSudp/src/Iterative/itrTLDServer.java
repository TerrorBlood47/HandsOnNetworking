package Iterative;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class itrTLDServer {
	public static int PORT = 3800;
	public static String IPaddress = "localhost";
	
	public static HashMap<String, DNS_Record > dnsRecords = new HashMap<>();
	
	public static void main(String[] args) {
		// Add A type records
		dnsRecords.put("apple.ac.bd", new DNS_Record("apple.ac.bd", "17.253.144.10", "A", 86400));
		dnsRecords.put("microsoft.ac.bd", new DNS_Record("microsoft.ac.bd", "40.76.4.15", "A", 86400));
		dnsRecords.put("netflix.ac.bd", new DNS_Record("netflix.ac.bd", "45.57.151.12", "A", 86400));
		dnsRecords.put("instagram.ac.bd", new DNS_Record("instagram.ac.bd", "52.0.76.128", "A", 86400));
		dnsRecords.put("ebay.ac.bd", new DNS_Record("ebay.ac.bd", "66.211.181.161", "A", 86400));
		dnsRecords.put("paypal.ac.bd", new DNS_Record("paypal.ac.bd", "64.4.250.39", "A", 86400));
		dnsRecords.put("spotify.ac.bd", new DNS_Record("spotify.ac.bd", "35.186.224.25", "A", 86400));
		dnsRecords.put("wordpress.ac.bd", new DNS_Record("wordpress.ac.bd", "192.0.78.9", "A", 86400));
		dnsRecords.put("bbc.ac.bd", new DNS_Record("bbc.ac.bd", "151.101.64.81", "A", 86400));
		dnsRecords.put("cnn.ac.bd", new DNS_Record("cnn.ac.bd", "151.101.1.67", "A", 86400));
		
		
		
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
		if (dnsRecords.containsKey(domainName)){
			DNS_Record record = dnsRecords.get(domainName);
			return record.value + " " + record.type ;
		}else{
			return "not found";
		}
	}
}
