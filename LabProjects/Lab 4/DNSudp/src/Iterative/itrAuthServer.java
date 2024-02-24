package Iterative;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class itrAuthServer {
	public static int PORT = 9800;
	public static String IPaddress = "localhost";
	
	public static HashMap<String, DNS_Record > dnsRecords = new HashMap<>();
	
	public static void main(String[] args) {
		// Populate the HashMap with DNS records
		dnsRecords.put("yahoo.ac.bd", new DNS_Record("yahoo.ac.bd", "98.137.246.7", "A", 86400));
		dnsRecords.put("bing.ac.bd", new DNS_Record("bing.ac.bd", "204.79.197.200", "A", 86400));
		dnsRecords.put("amazon.ac.bd", new DNS_Record("amazon.ac.bd", "176.32.103.205", "A", 86400));
		dnsRecords.put("facebook.ac.bd", new DNS_Record("facebook.ac.bd", "31.13.65.36", "A", 86400));
		dnsRecords.put("twitter.ac.bd", new DNS_Record("twitter.ac.bd", "104.244.42.129", "A", 86400));
		dnsRecords.put("stackoverflow.ac.bd", new DNS_Record("stackoverflow.ac.bd", "151.101.1.69", "A", 86400));
		dnsRecords.put("github.ac.bd", new DNS_Record("github.ac.bd", "140.82.121.4", "A", 86400));
		dnsRecords.put("wikipedia.ac.bd", new DNS_Record("wikipedia.ac.bd", "91.198.174.192", "A", 86400));
		dnsRecords.put("reddit.ac.bd", new DNS_Record("reddit.ac.bd", "151.101.1.140", "A", 86400));
		dnsRecords.put("linkedin.ac.bd", new DNS_Record("linkedin.ac.bd", "108.174.10.10", "A", 86400));
		
		// Add AAAA type records
		dnsRecords.put("example1.ac.bd", new DNS_Record("example1.ac.bd", "2001:0db8:85a3:0000:0000:8a2e:0370:7334", "AAAA", 86400));

		// Add NS type records
		dnsRecords.put("example2.ac.bd", new DNS_Record("example2.ac.bd", "ns1.example.ac.bd", "NS", 86400));
		dnsRecords.put("example3.ac.bd", new DNS_Record("example3.ac.bd", "ns2.example.ac.bd", "NS", 86400));

		// Add MX type records
		dnsRecords.put("example4.ac.bd", new DNS_Record("example4.ac.bd", "10 mail.example.ac.bd", "MX", 86400));
		
		
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
