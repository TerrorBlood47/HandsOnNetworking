

import java.io.*;
import java.nio.file.Files;
import java.text.*;
import java.util.*;
import java.net.*;

// Server class
public class Server
{
	public static void main(String[] args) throws IOException
	{
		// server is listening on port 5056
		ServerSocket ss = new ServerSocket(25000);
		System.out.println("Server Established");
		
		// running infinite loop for getting
		// client request
		while (true)
		{
			Socket s = null;
			
			try
			{
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
				
			}
			catch (Exception e){
				s.close();
				e.printStackTrace();
			}
		}
	}
}

// ClientHandler class
class ClientHandler extends Thread
{
	final ServerSocket ss;
	final DataInputStream dis;
	final DataOutputStream dos;
	final Socket s;
	
	final ObjectOutputStream oos;
	
	final ObjectInputStream ois;
	
	
	// Constructor
	public ClientHandler( ServerSocket ss, Socket s, DataInputStream dis, DataOutputStream dos, ObjectOutputStream oos, ObjectInputStream ois )
	{
		this.ss = ss;
		this.s = s;
		this.dis = dis;
		this.dos = dos;
		this.oos = oos;
		this.ois = ois;
	}
	
	@Override
	public void run()
	{
		String received;
		
		int window_size = 0;
		
		int timeOut = 3000; //3 seconds
		
		//turning the file data into bits
		//String filepath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "a.txt";
		File file = new File("C:\\Users\\azmai\\OneDrive\\Desktop\\HandsOnNetworking\\LabProjects\\Lab 5\\flow_control_lab\\src\\a.txt");
		byte[] fileBytes = null;
		
		try {
			fileBytes = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
		}
		
		while ( true ){
			try {
				received = dis.readUTF();
				System.out.println(received);
				
				if(received.equals("hello")){
					dos.writeInt((int) file.length());
					System.out.println("total byte num: " + file.length());
					window_size = dis.readInt();
					break;
				}
				
			} catch (IOException e) {
				System.out.println(e.getCause());
				e.printStackTrace();
			}
		}
		int i = 0;
		int iteration = window_size / TCPPacket.getTCPPacketSize();
		
		
		while (true)
		{
			try {
				//ss.setSoTimeout(3000);
				
				int start_time = (int) System.currentTimeMillis();
				
				for(int j=0; j<iteration; j++) {
					
					if(i==file.length()){
						break;
					}
					assert fileBytes != null;
					byte byteNum = fileBytes[ i ];
					
					int srcPort = this.ss.getLocalPort();
					int destPort = this.s.getPort();
					int flag = 1; // 1-> packet , 0->acknowledgement
					
					TCPPacket packetHeader = new TCPPacket(srcPort, destPort, i, 0, flag, window_size);
					
					//sending header
					oos.writeObject(packetHeader);
					
					//sending byte
					dos.writeByte(byteNum);
					dos.flush();
					System.out.println("sent byte no " + ( i ));
					i++;
				}
				int ack = 0;
				i = i - iteration;
				
				
				try{
					
					TCPPacket receivedPacket = (TCPPacket) ois.readObject();
					
					int ackReceivingTime = (int) System.currentTimeMillis();

					int delay = ackReceivingTime - start_time;

					System.out.println("delay : " + delay + " ms");

					if(delay > timeOut){
						throw new SocketTimeoutException();
					}
					
					timeOut = EWMA.getTimeOutInterval(delay);
					System.out.println("timeout : " + timeOut);
					
					ack = (int) receivedPacket.getAcknowledgmentNumber();
					System.out.println("header: " + receivedPacket);
					System.out.println("received byte acknowledgement : " + ack);
					
					if(ack >= file.length()){
						//whole file is received by the receiver
						System.out.println("whole file is received by the receiver");
						break;
					}
					
					i = ack;
					System.out.println("i = " + i);
					
				}catch (SocketTimeoutException e){
					System.out.println("did not receive anything for 3 second");
					continue;
				} catch (ClassNotFoundException e) {
					System.out.println(e.getCause());
					e.printStackTrace();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try
		{
			// closing resources
			this.dis.close();
			this.dos.close();
			oos.close();
			ois.close();
			this.s.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
