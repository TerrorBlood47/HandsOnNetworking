import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private Socket clientSocket;
	private DataInputStream in;
	private DataOutputStream out;
	
	private Scanner scanner;
	
	public Client() {
		
		try {
			this.clientSocket = new Socket("localhost", Server.PORT);
			System.out.println("Client Connected");
			this.in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
			this.out = new DataOutputStream(clientSocket.getOutputStream());
			
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		this.scanner = new Scanner(System.in);
		
		getFiles();
	}
	
	private void getFiles(){
		try {
			String fileLen = in.readUTF();
			System.out.println("file lenght : " + fileLen);
			int maxfiles = Integer.parseInt(fileLen);
			String menu = in.readUTF();
			System.out.println(menu);
			
			int userSelection = 1;
			boolean isSelectionCorrect = false;
			
			while ( !isSelectionCorrect ){
				System.out.println("Select File Number ");
				userSelection = scanner.nextInt();
				isSelectionCorrect = userSelection>0 && userSelection<=maxfiles;
			}
			
			out.writeUTF(""+ userSelection);
			String fileContent = in.readUTF();
			
			System.out.println(" -- FILE START -- ");
			System.out.println(fileContent);
			System.out.println(" -- FILE END -- ");
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main( String[] args ) {
		new Client();
	}
}
