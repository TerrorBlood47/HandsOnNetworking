import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ClientHandler extends Thread {
	
	Socket socket;
	
	DataInputStream dis;
	DataOutputStream dos;
	
	String username;
	
	public ClientHandler( Socket socket, DataInputStream dis, DataOutputStream dos ) {
		this.socket = socket;
		this.dis = dis;
		this.dos = dos;
		
	}
	
	public void run() {
		String received;
		String toReturn;
		
		while ( true ) {
			try {
				dos.writeUTF("provide username (no whitespace allowed) : ");
				this.username = dis.readUTF();
				
				if(username.matches(".*\\s+.*")){
					dos.writeUTF("Invalid username | provide correct username \n");
				}
				else{
					dos.writeUTF("username inserted");
					break;
				}
				
			} catch (IOException e) {
				System.out.println("Exception while taking username !!!");
				
			}
		}
		
		try {
			while ( true ) {
				
				// Ask user what he wants
				StringBuilder askFromClientToChoose = new StringBuilder("What do you want?\n");
				
				File[] fileList = new File(Server.FILE_PATH).listFiles();
				
				for( int i = 0; i< Objects.requireNonNull(fileList).length; i++){
					askFromClientToChoose.append(( i + 1 )).append(" -> ").append(fileList[ i ].getName()).append("\n");
				}
				
				askFromClientToChoose.append("Type \'exit\' to exit the program");
				dos.writeUTF(askFromClientToChoose.toString());
				dos.flush();
				
				// receive the answer from client
				received = dis.readUTF();
				
				if ( received.equals("exit") ) {
					System.out.println("Client " + this.socket + "( " + username+ " )" +  " sends exit...");
					System.out.println("Closing this connection.");
					this.socket.close();
					System.out.println("Connection closed");
					break;
				}
				
				File file = null;
				
				int fileIndex = Integer.parseInt(received);
				fileIndex--;
				
				if(fileList.length == 0) {
					System.out.println("No file exists at the server");
				}
				else if(fileIndex >= fileList.length || fileIndex < 0){
					dos.writeUTF("Invalid input");
				}
				else{
					file = fileList[fileIndex];
				}
				
				if(file == null){
					System.out.println("File is Null");
					dos.writeUTF("No such file exists at the server");
					dos.flush();
				}
				else{
					sendFile(file);
				}
				

			}
		} catch (IOException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
		} catch (Exception e){
			System.out.println(e.getCause());
			e.printStackTrace();
		}
		
	}
	
	public void sendFile( File file ) throws IOException {
		
		//sending filename
		dos.writeUTF(file.getName());
		dos.flush();
		
		int bytes = 0;
		
		FileInputStream fileInputStream
				= new FileInputStream(file.getAbsolutePath());
		
		System.out.println(file.getAbsolutePath());
		
		// Here we send the File to Server
		
		//send file size
		dos.writeLong(file.length());
		dos.flush();
		
		// Here we  break file into chunks
		byte[] buffer = new byte[400 * 1024];
		while ((bytes = fileInputStream.read(buffer))
				!= -1) {
			// Send the file to Server Socket
			dos.write(buffer, 0, bytes);
			dos.flush();
			System.out.println(buffer);
		}
		// close the file here
		fileInputStream.close();
		
		System.out.println(file.getName() + " has been received by " + this.username + " " + this.socket);
		
 	}
}
