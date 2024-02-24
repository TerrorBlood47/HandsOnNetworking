package Recursive;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class recRootServer {
	public static int PORT = 1505;
	public static String IPaddress = "localhost";
	
	public static HashMap<String, DNS_Record> dnsRecords = new HashMap<>();
	
	public static void main(String[] args) {
		// Add A type records
		dnsRecords.put("google.ac.bd", new DNS_Record("google.ac.bd", "123.345.55.4", "A", 86400));
		dnsRecords.put("example.ac.bd", new DNS_Record("example.ac.bd", "192.0.2.1", "A", 86400));
		dnsRecords.put("openai.ac.bd", new DNS_Record("openai.ac.bd", "203.0.113.5", "A", 86400));
		
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
				System.out.println(clientPort);
				
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
			DNS_Record record = dnsRecords.get(domainName);
			return record.value + " " + record.type;
		}
		else{
			//sent to root server
			byte[] sendData = domainName.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(recTLDServer.IPaddress), recTLDServer.PORT);
			serverSocket.send(sendPacket);
			
			System.out.println("Sent DNS response to TLD server" );
			
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			serverSocket.receive(receivePacket);
			
			String message = new String(receivePacket.getData(),0,receivePacket.getLength());
			
			System.out.println("Received DNS response from TLD server :" +  message);
			System.out.println(receivePacket.getPort());
			
			
			
			return message;
		}
	}
}
