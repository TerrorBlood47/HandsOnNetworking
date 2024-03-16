import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.security.PublicKey;

// Server class
public class Server {
	
	public static short PORT = 25005;
	
	public static void main( String[] args ) throws IOException {
		// server is listening
		ServerSocket ss = new ServerSocket(PORT);
		System.out.println("Server Established");
		
		// running infinite loop for getting
		// client request
		while ( true ) {
			Socket s = null;
			
			try {
				// socket object to receive incoming client requests
				s = ss.accept();
				
				System.out.println("A new client is connected : " + s);
				
				// obtaining input and out streams
				DataInputStream dis = new DataInputStream(s.getInputStream());
				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
				
				
				System.out.println("Assigning new thread for this client");
				
				// create a new thread object
				Thread t = new ClientHandler(ss, s, dis, dos, oos, ois);
				
				// Invoking the start() method
				t.start();
				
			} catch (Exception e) {
				s.close();
				e.printStackTrace();
			}
		}
	}
}



