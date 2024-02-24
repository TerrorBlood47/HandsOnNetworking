

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BankClient {
	public static void main(String[] args) {
		try {
			Socket socket = new Socket("192.168.1.102", 15000);
			System.out.println("Connected to server ");
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			
			
			
			Scanner scanner = new Scanner(System.in);
			
			while (true) {
				System.out.println("Enter id : ");
				String id = scanner.nextLine();
				System.out.println("Enter password : ");
				String pass = scanner.nextLine();
				
				oos.writeObject(id + " " + pass);
				String reply = ois.readObject().toString();
				System.out.println();
				System.out.println(reply);
				
				if (reply.equals("welcome")) {
					break;
				}
				
			}
			
			while (true) {
				System.out.println("\nWhat do u want to do? : ");
				System.out.println("1. Withdraw");
				System.out.println("2. balance");
				System.out.println("3. cashin");
				System.out.println("4. exit");
				System.out.println();
				System.out.println();
				
				String in = scanner.nextLine();
				
				oos.writeObject(in);
				oos.flush();
				
				String serverMsg = (String) ois.readObject();
				
				if(serverMsg.equals("goodbye")){
					break;
				}else if(serverMsg.equals("duplicate")){
					continue;
				}
				System.out.println("\n" + serverMsg);
				
			}
			
			oos.close();
			ois.close();
			socket.close();
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
