
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		try {
			// a socket to connect to server
			Socket socket = new Socket("192.168.1.102", 15000);
			System.out.println("Connected to server 1");
			
			//objects created for sending and receiving data
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			
			//for taking clients inputs
			Scanner scanner = new Scanner(System.in);
			
			
			while (true) {
				System.out.println("What do u want to know? : ");
				String in = scanner.nextLine();
				
				//sending messages to server
				oos.writeObject(in);
				
				//reading messages sent from servers
				String serverMsg = (String) ois.readObject();
				System.out.println(serverMsg);
				
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}