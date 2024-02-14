package Task1;// Java program to illustrate Client side
// Implementation using DatagramSocket 
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
		
		InetAddress ip = InetAddress.getByName("10.42.0.63");
		byte buf[] = null;
		
		// loop while user not enters "bye"
		while (true) {
			System.out.println("ENter");
			String inp = sc.nextLine();
			
			// convert the String input into the byte array.
			buf = inp.getBytes();
			
			// Step 2 : Create the datagramPacket for sending
			// the data.
			DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, 3333);
			
			// Step 3 : invoke the send call to actually send
			// the data.
			ds.send(DpSend);
			
			byte bufr[] = new byte[4 * 1024];
			DatagramPacket dpRecieve = new DatagramPacket(bufr, bufr.length);
			
			ds.receive(dpRecieve);
			
			String recieve = new String(dpRecieve.getData(), 0, dpRecieve.getLength());
			
			System.out.println("recieved : " + recieve);
			
			// break the loop if user enters "bye"
			if (inp.equals("bye"))
				break;
		}
	}
}
