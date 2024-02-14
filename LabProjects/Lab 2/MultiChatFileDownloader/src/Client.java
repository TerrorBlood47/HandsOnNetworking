import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	public static DataInputStream dis = null;
	public static DataOutputStream dos = null;
	
	public static void main( String[] args ) {
		try {
			Socket socket = new Socket("localhost", Server.PORT);
			 dis = new DataInputStream(socket.getInputStream());
			 dos = new DataOutputStream(socket.getOutputStream());
			
			Scanner scn = new Scanner(System.in);
			String username = null;
			
			while ( true ) {
				System.out.println(dis.readUTF());
			    username=scn.nextLine();
				
				dos.writeUTF(username);
				dos.flush();
				
				String userNameAcMsg = dis.readUTF();
				
				if(userNameAcMsg.toLowerCase().equals("invalid username | provide correct username \n")){
					continue;
				}
				else if(userNameAcMsg.equals("username inserted")){
					break;
				}
			}
			
			
			// the following loop performs the exchange of
			// information between client and client handler
			while (true)
			{
				System.out.println(dis.readUTF());
				String tosend = scn.nextLine();
				dos.writeUTF(tosend);
				
				// If client sends exit,close this connection
				// and then break from the while loop
				if(tosend.equals("exit"))
				{
					System.out.println("Closing this connection : " + socket);
					socket.close();
					System.out.println("Connection closed");
					break;
				}
				
				
				// printing date or time as requested by client
				String receiveFileName = dis.readUTF();
				
				if ( receiveFileName.equals("No file exists at the server") ){
					System.out.println(receiveFileName);
					continue;
				} else if ( receiveFileName.equals("No such file exists at the server") ) {
					System.out.println(receiveFileName);
					continue;
				}
				
				System.out.println(receiveFileName);
				
				receiveFile(receiveFileName);
			}
			
			// closing resources
			scn.close();
			dis.close();
			dos.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getCause());
		}
	}
	
	private static void receiveFile(String fileName)
			throws FileNotFoundException {
		try {
			//set filepath to store
			
			//get working directory
			String workingDirectory = System.getProperty("user.dir");
			System.out.println(workingDirectory);
			
			String saveFilePath = workingDirectory
									+File.separator
									+"MultiChatFileDownloader"
									+File.separator
									+"src"
									+File.separator
									+"clientstorage"
									+File.separator
									+"Received_"+System.currentTimeMillis()+"_"+fileName;
			
			
			int bytes = 0;
			
			//size of the file
			long size = dis.readLong();
			System.out.println(size + " bytes has been received");
			
			FileOutputStream fileOutputStream
					= new FileOutputStream(saveFilePath);
			
			byte[] buffer = new byte[ 400 * 1024 ];
			while ( size > 0
					&& ( bytes = dis.read(
					buffer, 0,
					(int) Math.min(buffer.length, size)) )
					!= - 1 ) {
				
				// Here we write the file using write method
				fileOutputStream.write(buffer, 0, bytes);
				size -= bytes; // read upto file size
			}
			
			
			// Here we received file
			System.out.println("File is Received");
			fileOutputStream.close();
			
		} catch (IOException e) {
			System.out.println("oh no "+e.getCause());
			e.printStackTrace();
		}
	}
}
