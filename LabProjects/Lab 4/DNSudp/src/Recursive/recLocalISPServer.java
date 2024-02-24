package Recursive;

import Recursive.recRootServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class recLocalISPServer {
	public static int PORT = 5905;
	public static HashMap< String, DNS_Record > dnsRecords = new HashMap<>();
	
	public static long start_time =  System.currentTimeMillis();
	
	public static void main( String[] args ) {
		// Add CNAME type records
		dnsRecords.put("www.google.ac.bd", new DNS_Record("www.google.ac.bd", "google.ac.bd", "CNAME", 86400));
		dnsRecords.put("www.example.ac.bd", new DNS_Record("www.example.ac.bd", "example.ac.bd", "CNAME", 86400));
		dnsRecords.put("www.openai.ac.bd", new DNS_Record("www.openai.ac.bd", "openai.ac.bd", "CNAME", 86400));
		
		
		
		try {
			DatagramSocket serverSocket = new DatagramSocket(PORT);
			System.out.println("DNS Server Established");
			
			while ( true ) {
				byte[] receiveData = new byte[ 1024 ];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				
				//Thread.sleep(2000);
				
				String domainName = new String(receivePacket.getData(), 0, receivePacket.getLength());
				domainName = extractDomainName(domainName);
				InetAddress clientAddress = receivePacket.getAddress();
				int clientPort = receivePacket.getPort();
				
				System.out.println("Received DNS request for: " + domainName);
				System.out.println(clientPort);
				
				String ipAddress = handleDNSRequest(serverSocket,domainName);
				
				System.out.println("Sent DNS response: " + ipAddress);
				
				String parts[] = ipAddress.split(" ");
				
				if(parts[1].equals("CNAME")){
					ipAddress = handleDNSRequest(serverSocket,parts[0]);
					System.out.println("Sent DNS response: " + ipAddress);
				}
				
				byte[] sendData = ipAddress.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
				serverSocket.send(sendPacket);
				
				if(!ipAddress.equals("not found"))AddToCache(domainName, parts[0], parts[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static void AddToCache( String domainName,String value, String type ) {
		if(! dnsRecords.containsKey(domainName)){
			DNS_Record record = new DNS_Record(domainName,value,type,8);
			System.out.println("Adding record to cache: " + record);
			dnsRecords.put(domainName, record);
		}
		
		DeleteRecordsFromCache();
	}
	
	private static void DeleteRecordsFromCache(){
		long curr_time = System.currentTimeMillis();
		
		long delay = (curr_time - start_time) / 1000; // Computing the delay in seconds
		System.out.println("delay : " + delay);
		
		// Using iterator to remove elements during iteration
		Iterator< Map.Entry< String, DNS_Record > > iterator = dnsRecords.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry< String, DNS_Record > entry = iterator.next();
			DNS_Record record = entry.getValue();
			
			// Update TTL
			record.ttl = (int) (record.ttl - (int)delay);
			
			
			if (record.ttl <= 0) {
				// Remove the entry from the HashMap
				System.out.println("removing record : " + record);
				iterator.remove();
			}
		}
		
		//restart timer
		start_time =  System.currentTimeMillis();
	}
	
	private static String handleDNSRequest( DatagramSocket serverSocket, String domainName ) throws IOException {
		if ( dnsRecords.containsKey(domainName) ) {
			DNS_Record record = dnsRecords.get(domainName);
			return record.value + " " + record.type;
		} else {
			//sent to root server
			byte[] sendData = domainName.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(recRootServer.IPaddress), recRootServer.PORT);
			serverSocket.send(sendPacket);
			
			System.out.println("Sent DNS response to root server");
			
			byte[] receiveData = new byte[ 1024 ];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			
			String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
			
			System.out.println("Received DNS response from root server :" + message);
			System.out.println(receivePacket.getPort());
			
			
			return message;
			
			
		}
	}

	private static String extractDomainName(String dnsRequest) {
		String[] parts = dnsRequest.split("\\s+");

		if (parts.length > 4) {
			return parts[4];
		} else {
			return null;
		}
	}
}
