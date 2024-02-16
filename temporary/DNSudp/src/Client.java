

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Client {
	public static void main(String args[]) throws IOException {
		Scanner sc = new Scanner(System.in);
		
		// Step 1:Create the socket object for
		// carrying the data.
		DatagramSocket ds = new DatagramSocket();
		
		InetAddress ip = InetAddress.getByName("localhost");
		byte buf[] = null;
		
		// loop while user not enters "bye"
		while (true) {
			System.out.println("ENter");
			String inp = sc.nextLine();
			
			// break the loop if user enters "bye"
			if (inp.equals("bye"))
				break;
			
			// convert the String input into the byte array.
			buf = inp.getBytes();
			
			// Step 2 : Create the datagramPacket for sending
			// the data.
			DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, Recursive.recLocalISPServer.PORT);
			
			// Step 3 : invoke the send call to actually send
			// the data.
			ds.send(DpSend);
			
			byte bufr[] = new byte[1024];
			DatagramPacket dpRecieve = new DatagramPacket(bufr, bufr.length);
			
			ds.receive(dpRecieve);
			
			String recieve = new String(dpRecieve.getData(), 0, dpRecieve.getLength());
			
			System.out.println("recieved : " + recieve);
			
			
		}
	}
}
