import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
	
	public static void main( String[] args )  {
		try {
			ServerSocket serverSocket = new ServerSocket(22222);
			System.out.println("Server Connected");
			int i=1;
			
			while ( true ){
				Socket socket = serverSocket.accept();
				System.out.println("Client connected...");
				clientHandlers.add(new ClientHandler(socket,"user " + Integer.toString(i)));
				i++;
			}
			
			
		} catch (IOException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
		}
		
	}
}
