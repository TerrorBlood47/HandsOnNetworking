import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	
	public static final int PORT = 7500;
	public static final String FILE_PATH ="C:\\Users\\azmai\\IdeaProjects\\DemoFileTransferMultiClient\\src\\serverstorage" ;
	
	public static void main( String[] args ) throws IOException {
		
		ServerSocket serverSocket = new ServerSocket(PORT);
		System.out.println("Server Established");
		
		while(true){
			try{
				Socket socket = serverSocket.accept();
				System.out.println("Client Connected " + socket);
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				
				Thread t = new ClientHandler(socket,dis,dos);
				
				t.start();
				
			}catch (IOException e){
				System.out.println(e.getCause());
				e.printStackTrace();
			}
		}
		
	
	}
}