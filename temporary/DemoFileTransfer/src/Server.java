import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	public static final String FILES_PATH = "C:\\Users\\azmai\\IdeaProjects\\DemoFileTransfer\\src\\files";
	private ServerSocket serverSocket;
	public static final int PORT = 3030;
	
	public Server(){
		try{
			serverSocket = new ServerSocket(PORT);
			
			acceptConnections();
			
		} catch (IOException e) {
			System.out.println("Server Crashed");
		}
	}
	
	private void acceptConnections() {
		while(true){
			try {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connected");
				if(clientSocket.isConnected()){
					new Thread( () -> {
						ClientConnection client = new ClientConnection(clientSocket);
						client.sendFile();
						
					}).start();
				}
				
			} catch (IOException e) {
				System.out.println("Client Connection Interrupted");
			}
			
			
		}
		
	}
	
	public static void main( String[] args ) {
		new Server();
	
	}
}