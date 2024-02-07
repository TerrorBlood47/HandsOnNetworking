import java.io.*;
import java.net.Socket;
import java.nio.file.NoSuchFileException;
import java.time.LocalDate;
import java.util.Objects;

public class ClientHandler extends Thread {
	Socket socket;
	DataInputStream dis;
	DataOutputStream dos;


	public ClientHandler( Socket socket, DataInputStream dis, DataOutputStream dos ) {
		this.socket = socket;
		this.dis = dis;
		this.dos = dos;
		
	}
	
	public void run() {
		
		try {
			
			// Ask user what he wants
			StringBuilder askFromClientToChoose = new StringBuilder("What do you want?[get | post]\n");
			
			askFromClientToChoose.append("Currently the Server has these resources\n");
			
			File[] fileList = new File(httpServer.FILE_PATH).listFiles();
			
			if(fileList.length == 0){
				askFromClientToChoose.append("[Empty] \n\n");
			}
			
			for ( int i = 0; i < Objects.requireNonNull(fileList).length; i++ ) {
				askFromClientToChoose.append(fileList[ i ].getName()).append("\n");
			}
			
			askFromClientToChoose.append("write the name of the file(including the file extension)" +
					"if you want to download.....choose correctly.....\n" +
					"or you can upload instead");
			
			dos.writeUTF(askFromClientToChoose.toString());
			dos.flush();
			
			//get clients request
			String request = dis.readUTF();
			System.out.println( request);
			
			if ( request.startsWith("GET") ) {
				
				System.out.println("hello\n");
				String filename = Utility.getStringPortion(request,"GET /", " HTTP/1.0");
				System.out.println("fn : " + filename);
				System.out.println(request);
				
				//get file path
				String workingDirectory = System.getProperty("user.dir");
				
				String filePath = workingDirectory + File.separator+"src"
						+File.separator +"ServerStorage"+File.separator + filename;
				
				try {
					File file = new File(filePath);
					int lastDot = filePath.lastIndexOf(".");
					
					String fileType = null;
					
					if ( lastDot != - 1 ) { // Check if there is a dot in the string
						fileType = filePath.substring(lastDot + 1);
					} else {
						fileType = "text";
					}
					
					String metaData = "HTTP/1.0 200 OK\r\n"
							+ "Content-Type: " + fileType + "\r\n"
							+ "Content-Length: " + file.length() + "\r\n"
							+ "Date: " + LocalDate.now() + "\r\n"
							+ "Content-Disposition: attachment; filename=" + file.getName() + "\r\n"
							+ "\r\n";
					
					//sending metadata
					dos.writeUTF(metaData);
					dos.flush();
					
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
					
					System.out.println("sent " + file.getName() + " to " + this.socket);
				} catch (NullPointerException | NoSuchFileException e) {
					int lastDot = filePath.lastIndexOf(".");
					
					String fileType = null;
					
					if ( lastDot != - 1 ) { // Check if there is a dot in the string
						fileType = filePath.substring(lastDot + 1);
					} else {
						fileType = "text";
					}
					
					String errorMetaData = "HTTP/1.0 404 Not Found\r\n" +
							"Date: " + LocalDate.now() + "\r\n" +
							"Content-Type: " + fileType + "\r\n" +
							"\r\n";
					
					dos.writeUTF(errorMetaData);
					dos.flush();
				}
				
		
		}
			else if ( request.startsWith("POST") ) {
				
				
				System.out.println(request + "from "+ this.socket);
				
				String okFileMsg = "HTTP/1.0 200 OK";
				System.out.println(okFileMsg);
				dos.writeUTF(okFileMsg);
				dos.flush();
				
				String filename = Utility.getStringPortion(request,"POST /",  " HTTP/1.0\n");
				String contentLength = Utility.getStringPortion(request, "Content-Length: ", "\r\n");
				
				int bytes = 0;
				long size =  Integer.parseInt(contentLength);
				
				String saveFilePath = httpServer.FILE_PATH + File.separator + "Received_" + filename;
				
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
				System.out.println(contentLength + " bytes has been received");
				fileOutputStream.close();
				
			
			} else {
				System.out.println("Invalid request from " + this.socket);
				dos.writeUTF("HTTP/1.0 405 Method Not Allowed\n");
			}
			
			socket.close();
			
			
		} catch (IOException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
			
		} catch (Exception e) {
			System.out.println(e.getCause());
			e.printStackTrace();
		}
		
	}
	

}
