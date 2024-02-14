package Task1;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashMap;

public class RootServer{
	public static int PORT = 7000;
	
	public static HashMap<String, String> localStorage = new HashMap<>();
	
	public static void main(String[] args) throws Exception {
		
		localStorage.put("google.ac.bd", "123.345.55.4");
		localStorage.put("example.ac.bd", "192.0.2.1");
		localStorage.put("openai.ac.bd", "203.0.113.5");
		localStorage.put("flipkart.ic.bd", "12.65.847.87");
		localStorage.put("tata.ic.bd", "54.54.323.32");
		
		// Create a DatagramSocket
		DatagramSocket serverSocket = new DatagramSocket(PORT);
		System.out.println("Server Established");
		
		while (true) {
			// Receive a message from the client
			byte[] buffer = new byte[1024 * 4];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			System.out.println(packet);
			serverSocket.receive(packet);
			System.out.println(packet);
			String message = new String(packet.getData(), 0, packet.getLength());
			System.out.println("Client message: " + message);
			
			new Thread(new UDPhandler(serverSocket, packet)).start();
		}
		
	}
	
	private static class UDPhandler implements Runnable {
		
		DatagramSocket serverSocket;
		DatagramPacket receivedPacket;
		
		public UDPhandler(DatagramSocket serverSocket, DatagramPacket receivedPacket) {
			this.serverSocket = serverSocket;
			this.receivedPacket = receivedPacket;
		}
		
		@Override
		public void run() {
			try {
				// byte[] sendData = handleDNSrequests(receivedPacket.getData());
				byte[] sendData = handleDomainReq(receivedPacket.getData());
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
						this.receivedPacket.getAddress(), this.receivedPacket.getPort());
				
				this.serverSocket.send(sendPacket);
				System.out.println("Data sent : " + sendPacket);
				System.out.println(Arrays.toString(sendPacket.getData()));
				
			} catch (IOException e) {
				System.out.println("Sending failed.....");
				System.out.println(e.getCause());
				e.printStackTrace();
			}
			
		}
		
		private byte[] handleDomainReq(byte[] data) throws SocketException, SecurityException {
			
			try {
				
				DatagramPacket packet = new DatagramPacket(data, data.length);
				serverSocket.receive(packet);
				String message = new String(packet.getData(), 0, packet.getLength());
				
				// String[] parts = message.split(".");
				
				// int len = parts.length;
				
				if (localStorage.containsKey(message)) {
					
					String ip = localStorage.get(message);
					
					byte[] buf = ip.getBytes();
					
					DatagramPacket DpSend = new DatagramPacket(buf, buf.length, receivedPacket.getAddress(),
							receivedPacket.getPort());
					
					serverSocket.send(DpSend);
					
				} else {
					String ip = "error";
					
					byte[] buf = ip.getBytes();
					
					DatagramPacket DpSend = new DatagramPacket(buf, buf.length, receivedPacket.getAddress(),
							receivedPacket.getPort());
					
					serverSocket.send(DpSend);
					
				}
				
			} catch (Exception e) {
				System.out.println(e);
			}
			
			return data;
			
		}
		
		// private byte[] handleDNSrequests( byte[] data ) {
		//
		// try {
		// DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
		// short id = dis.readShort();
		// System.out.println("id : " + id);
		// short flag = dis.readShort();
		// System.out.println("flag: " + flag);
		//
		// int question_count = dis.readShort();
		// System.out.println("ques count : " + question_count);
		//
		// int ans_record_count = dis.readShort();
		// System.out.println("ans_record_count : " + ans_record_count);
		//
		// int auth_record_count = dis.readShort();
		// System.out.println("auth_record_count : " + auth_record_count);
		//
		// int additional_record_count = dis.readShort();
		// System.out.println("additional record count : " + additional_record_count);
		//
		//
		//
		//
		// } catch (IOException e) {
		// System.out.println("data problem in handling dns request.....");
		// System.out.println(e.getCause());
		// e.printStackTrace();
		// }
		//
		// return data;
		// }
	}
	
}
