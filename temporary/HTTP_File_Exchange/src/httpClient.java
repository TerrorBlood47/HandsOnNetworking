import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class httpClient {
	
	private static String FILE_PATH = "C:\\Users\\azmai\\IdeaProjects\\HTTP_File_Exchange\\src\\multipleClientsStorage";
	
	public static DataInputStream dis = null;
	public static DataOutputStream dos = null;
	
	public static void main( String[] args ) {
		
		
		try {
			Socket socket = new Socket("localhost", httpServer.PORT);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			
			Scanner scn = new Scanner(System.in);
			
			System.out.println("choose ? [get | post]");
			String tosendReq = scn.nextLine();
			
			if(tosendReq.toLowerCase().startsWith("get")){
				
				String serverAsking = dis.readUTF();
				System.out.println(serverAsking);
				
				if(serverAsking.contains("[Empty]")){
					socket.close();
					return;
				}
				
				//send to server the filename to download
				String chosenFileName = scn.nextLine();
				System.out.println(chosenFileName);
				
				String getReq ="GET /" + chosenFileName + " HTTP/1.0";
				dos.writeUTF(getReq);
				dos.flush();
				System.out.println(getReq);
				
				Thread.sleep(5);
				
				
				//receive metadata
				String metaData = dis.readUTF();
				System.out.println(metaData);
				
				if ( metaData.startsWith("HTTP/1.0 404 Not Found") ) {
					System.out.println(metaData);
					socket.close();
				}
				
				//receive contents of the file
				
				//get working directory
				String workingDirectory = System.getProperty("user.dir");
				System.out.println(workingDirectory);
				
				String saveFilePath = workingDirectory
						+File.separator
						+"src"
						+File.separator
						+"multipleClientsStorage"
						+File.separator
						+"Received_"+ System.currentTimeMillis()+ "_" +chosenFileName;
				
				int bytes = 0;
				
				String contentLenght = Utility.getStringPortion(metaData,"Content-Length: ", "\r\n" );
				
				long size = Long.parseLong(contentLenght);
				System.out.println(size + " bytes has been received");
				
				
				//get the contents of the file
				FileOutputStream fileOutputStream
						= new FileOutputStream(saveFilePath);
				
				byte[] buffer = new byte[ 4 * 1024 ];
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
			}
			else if(tosendReq.toLowerCase().startsWith("post")){
				
				String serverAsking = dis.readUTF();
				
				// Ask user what he wants
				StringBuilder askFromClientToChoose = new StringBuilder("What do you want to post?\n");
				
				File[] fileList = new File(httpClient.FILE_PATH).listFiles();
				
				for ( int i = 0; i < Objects.requireNonNull(fileList).length; i++ ) {
					askFromClientToChoose.append(fileList[ i ].getName()).append("\n");
				}
				
				askFromClientToChoose.append("""
						write the name of the file(including the file extension)
						 you want to upload.....
						choose correctly:\s""");
				
				System.out.println(askFromClientToChoose);
				
				if(askFromClientToChoose.toString().contains("[Empty]")){
					socket.close();
					return;
				}
				
				//take input from client
				String chosenFileName = scn.nextLine();
				
				String workingDirectory = System.getProperty("user.dir");
				String filePath = workingDirectory + File.separator + "src" + File.separator + "multipleClientsStorage"
						+ File.separator + chosenFileName;
				
				try{
					File file = new File(filePath);
					
					//send a post request to client
					String postRequest = "POST /"+chosenFileName+" HTTP/1.0\n" + "Content-Length: " + file.length()+"\r\n";
					dos.writeUTF(postRequest);
					dos.flush();
					
					String serverResponse = dis.readUTF();
					
					if(serverResponse.startsWith("HTTP/1.0 200 OK")){
						System.out.println("Server accepted request to post\n");
						System.out.println(serverResponse);
						
						int bytes = 0;
						FileInputStream fileInputStream = new FileInputStream(filePath);
						
						byte[] buffer = new byte[4 * 1024];
						while ((bytes = fileInputStream.read(buffer))
								!= -1) {
							// Send the file to Server Socket
							dos.write(buffer, 0, bytes);
							dos.flush();
							System.out.println(buffer);
						}
						
						fileInputStream.close();
					}
					else{
						System.out.println("Error sending file !!!!");
						System.out.println(serverResponse);
					}
					
				}catch (NullPointerException e){
					System.out.println("No such file exists on your pc");
				}
				
				
			}else{
				System.out.println("Invalid Input\n");
			}
			
			
			// closing resources
			scn.close();
			dis.close();
			dos.close();
			System.out.println("Disconnecting from Server .....");
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getCause());
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	
}
