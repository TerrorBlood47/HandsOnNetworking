import java.io.*;
import java.io.File;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;

public class ClientConnection {
	
	private Socket clientSocket;
	private DataInputStream in;
	private DataOutputStream out;
	
	public ClientConnection( Socket clientSocket ) {
		
		try {
			this.clientSocket = clientSocket;
			this.in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
			this.out = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
			this.out = new DataOutputStream(clientSocket.getOutputStream());
			
		} catch (IOException e) {
			System.out.println("Problem in client");
		}
	}
	
	public void sendFile() {
		sendMenu();
		int index = getSelectedFileIndex();
		sendSelectedFileIndex(index);
	}
	
	private void sendSelectedFileIndex( int index ) {
		File[] fileList = new File(Server.FILES_PATH).listFiles();
		File selectedFile = fileList[ index ];
		try {
			List< String > fileLines = Files.readAllLines(selectedFile.toPath());
			String fileContent = String.join("\n", fileLines);
			out.writeUTF(fileContent);
		} catch (IOException e) {
			System.out.println("Read file exception");
		}
	}
	
	private int getSelectedFileIndex() {
		try {
			String input = in.readUTF();
			return Integer.parseInt(input) - 1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return - 1;
	}
	
	private void sendMenu() {
		String menu = "** Files **\n";
		File[] fileList = new File(Server.FILES_PATH).listFiles();
		try {
			if ( fileList != null ) {
				out.writeUTF("" + fileList.length);
				for ( Integer i = 0; i < fileList.length; i++ ) {
					menu += String.format("* %d - %s\n", ( i + 1 ), fileList[ i ].getName());
				}
			} else {
				System.out.println("No files found in the directory.");
				out.writeUTF("0");
			}
			out.writeUTF(menu);
			System.out.println("menu sent");
			System.out.println(menu);
		} catch (IOException e) {
			System.out.println("file list sending problem");
		} catch (NullPointerException e) {
			e.printStackTrace();
			e.getMessage();
			System.out.println(e.getCause());
		}
	}
}
