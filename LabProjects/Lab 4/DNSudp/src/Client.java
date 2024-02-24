

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.Scanner;
import Iterative.*;
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
			System.out.println("Enter");
			String inp = sc.nextLine();
			inp = buildDnsRequest(inp);
			
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
			
			String receive = new String(dpRecieve.getData(), 0, dpRecieve.getLength());
			
			String received[] = receive.split(" ");
			
			if( !receive.equals("not found"))System.out.println("received : " + received[0]);
			else System.out.println(receive);
			
		}
	}

	private static String buildDnsRequest(String input) {

		// Header
		// Transaction ID: A unique identifier for the query
		String transactionId = "1234";

		// Flags: Placeholder flags for a standard query
		String flags = "0100";

		// Questions: Number of questions
		String questions = "0001";

		// Answer RRs: Number of answer resource records
		String answerRrs = "0000";

		// Query type: A (IPv4 address) record
		String queryType = "0001";

		// Query class: IN (Internet)
		String queryClass = "0001";

		String domainName = input;

		// Construct the DNS query string
		return transactionId + " " + flags + " " + questions + " " + answerRrs + " " + domainName + " " + queryType + " " + queryClass;
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
