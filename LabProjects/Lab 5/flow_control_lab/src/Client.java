
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

// Client class 
public class Client
{
	public static void main(String[] args) throws IOException
	{
		try
		{
			Scanner scn = new Scanner(System.in);
			Socket s = new Socket("localhost", 25000);
			
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			
			int iteration = 4;
			int window_size = iteration*TCPPacket.getTCPPacketSize();
			
			//starting 3-way handshaking
			System.out.println();
			System.out.println("Client starts the 3-way handshaking by sending 'Hello' ");
			dos.writeUTF("hello");
			int packet_no = dis.readInt();
			
			System.out.println("packet num : " + packet_no);
			
			dos.writeInt(window_size);
			System.out.println();
			
			//TreeMap automatically sorts the bytes based on seq number
			TreeMap< Integer, Byte > dataStorer = new TreeMap<>();
			int cumulative_ack = 0;
			
			
			try {
				while ( true ) {
					TCPPacket packetHeader = null;
					for ( int j = 0; j < iteration; j++ ) {
						packetHeader = (TCPPacket) ois.readObject();
						System.out.println("received packet header : " + packetHeader);
						Byte data = dis.readByte();
						//System.out.println("data : " + data);
						
						dataStorer.put(packetHeader.getSequenceNumber(), data);
						
						System.out.println("seq num : " + packetHeader.getSequenceNumber());
						//System.out.println("ack : " + cumulative_ack);
						
						if(packetHeader.getSequenceNumber() == cumulative_ack + 1){
							cumulative_ack = packetHeader.getSequenceNumber() ;
							//System.out.println("ack in: " + cumulative_ack);
							
							if(cumulative_ack==7){
								Thread.sleep(9000);
							}
						}
						
						if(cumulative_ack == packet_no-1){
							System.out.println("completed");
							break;
						}
					}
					
					int flag = 0;
					
					if ( cumulative_ack == packet_no - 1 ) {
						
						System.out.println("sending cumulative ack : " + (cumulative_ack + 1));
						oos.writeObject(new TCPPacket(s.getPort(), packetHeader.getSourcePort(), 0,
								cumulative_ack + 1, flag,window_size));
						
						System.out.println("Closing this connection : " + s);
						s.close();
						System.out.println("Connection closed");
						break;
					}
					
			
					//if(cumulative_ack > new Random().nextInt(10)) {
					System.out.println("sending cumulative ack : " + (cumulative_ack + 1));
					System.out.println();
						oos.writeObject(new TCPPacket(s.getPort(), packetHeader.getSourcePort(), 0,
								cumulative_ack + 1, flag, window_size));
						
					//}
					
				}
			}catch (EOFException e){
				System.out.println("EOF reached");
			}
			
			byte[] recieveBytes = new byte[packet_no];
			
			int i = 0;
			
			try {
//				for ( Map.Entry< Integer, Byte > entry : dataStorer.entrySet() ) {
//					recieveBytes[ i ] = entry.getValue();
//					i++;
//				}
				
				for(i=0; i<packet_no; i++){
					recieveBytes[i] = dataStorer.get(i);
				}
				
			}catch (ArrayIndexOutOfBoundsException e){
				System.out.println(e.getCause());
				e.printStackTrace();
			}
			
//			String file = new String(recieveBytes, StandardCharsets.UTF_8);
//			System.out.println(file);
			
			try (FileOutputStream fos = new FileOutputStream("Received_file.txt")) {
				
				fos.write(recieveBytes, 0 ,recieveBytes.length);
				System.out.println("Data has been written to the file.");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			scn.close();
			dis.close();
			dos.close();
			oos.close();
			ois.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
