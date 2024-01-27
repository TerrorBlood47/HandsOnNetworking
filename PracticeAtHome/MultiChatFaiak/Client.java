import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	
	public static void main( String[] args ) {
		try {
			Socket socket = new Socket("localhost", 22222);
			System.out.println("Client connected");
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			
			
			new WriterThread(oos);
			new ReaderThread(ois);
			
		} catch (IOException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
		}
	}
}
