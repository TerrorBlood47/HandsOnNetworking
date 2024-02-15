package Task1;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;

public class RootServer {
	public static int PORT = 7000;


	public static HashMap<String,String> localStorage = new HashMap<>();


	public static void main( String[] args ) throws Exception {

		localStorage.put("flipkart.ic.bd","12,12,65");
		localStorage.put("google.ac.bd", "123.345.55.4");
		localStorage.put("example.ac.bd", "192.0.2.1");
		localStorage.put("openai.ac.bd", "203.0.113.5");

		// Create a DatagramSocket
		DatagramSocket serverSocket = new DatagramSocket(PORT);
		System.out.println("Server Established");


		while ( true ) {
			// Receive a message from the client
			byte[] buffer = new byte[ 1024 * 4 ];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			System.out.println(packet);
			serverSocket.receive(packet);
			System.out.println(packet);
			String message = new String(packet.getData(), 0, packet.getLength());
			System.out.println("Client message: " + message);

			new Thread(new Roothandler(serverSocket, packet, message)).start();
		}

	}

	private static class Roothandler implements Runnable {

		DatagramSocket serverSocket;
		DatagramPacket receivedPacket;

		public static String rootIp = "localhost";
		public static int rootPort = 7000;

		String message = null;

		public Roothandler( DatagramSocket serverSocket, DatagramPacket receivedPacket, String m ) {
			this.serverSocket = serverSocket;
			this.receivedPacket = receivedPacket;
			this.message = m;
		}

		@Override
		public void run() {
			try {
				System.out.println("asche.....");
				//byte[] sendData = handleDNSrequests(receivedPacket.getData());
				byte[] sendData = handleDomainReq(receivedPacket.getData());
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, this.receivedPacket.getAddress()
						, this.receivedPacket.getPort());

				this.serverSocket.send(sendPacket);
				System.out.println("Data sent : " + sendPacket);
				System.out.println(Arrays.toString(sendPacket.getData()));

			} catch (IOException e) {
				System.out.println("Sending failed.....");
				System.out.println(e.getCause());
				e.printStackTrace();
			}

		}

		private byte[] handleDomainReq( byte[] data ) throws IOException {

			System.out.println("hoise " + this.message);

			if (localStorage.containsKey(this.message)) {
				System.out.println("paisiiii........");
				String IP = localStorage.get(this.message);
				System.out.println(IP);
				byte[] ip_data = IP.getBytes();
				return  ip_data;
			}else{
				System.out.println("painai");
//				DatagramPacket get_ip_data_in_packet = SendToRootServer();
//				String s = new String(get_ip_data_in_packet.getData(), 0, get_ip_data_in_packet.getLength());
				String s = "not found";
				return s.getBytes();
			}

		}

		private DatagramPacket SendToRootServer( ) throws IOException {
			byte[] sendData = this.message.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(rootIp)
					, rootPort);
			this.serverSocket.send(sendPacket);


			byte[] buffer = new byte[ 1024 * 4 ];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			serverSocket.receive(packet);

			return packet;

		}

	}


}

