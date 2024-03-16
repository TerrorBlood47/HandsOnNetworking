import java.io.*;
import java.net.Socket;
import java.util.*;

// Client class 
public class Client {
	
	
	
	public static void main( String[] args ) throws IOException {
		try {
			Scanner scn = new Scanner(System.in);
			Socket s = new Socket("localhost", Server.PORT);
			
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			
			Queue<TCPPacket> AckQueue = new PriorityQueue<>();
			
			//starting 3-way handshaking
			System.out.println();
			System.out.println("Client starts the 3-way handshaking by sending SYN packet ");
			
			TCPPacket SYN_packet = new TCPPacket((short) s.getPort(), Server.PORT, - 2, 0, (byte) ( 1 << 1 ));
			oos.writeObject(SYN_packet);
			
			TCPPacket SYNACK_packet = (TCPPacket) ois.readObject();
			System.out.println(SYNACK_packet);
			int packet_no = SYNACK_packet.getReceive_window();
			System.out.println("packet num : " + packet_no);
			
			TCPPacket SYNACK_ACK_packet = new TCPPacket((short) s.getPort(), Server.PORT, - 1, 0, (byte) ( 1 << 4 ), (short) 100);
			oos.writeObject(SYNACK_ACK_packet);
			System.out.println();
			
			//TreeMap automatically sorts the bytes based on seq number
			TreeMap< Integer, Byte > dataStorer = new TreeMap<>();
			int cumulative_ack = 0;
			
			
			
			
			
			int k = 3;
			try {
				while ( true ) {
					TCPPacket packet = null;
					
					int window_size = 1;
					
					for ( int j = 0; j < window_size; j++ ) {
						packet = (TCPPacket) ois.readObject();
						System.out.println("received packet header : " + packet);
						Byte data = packet.getData();
						
						window_size = packet.getReceive_window();
						//System.out.println("data : " + data);
						
						AckQueue.add( packet);
						dataStorer.put(packet.getSequenceNumber(), data);
						
						System.out.println("seq num : " + packet.getSequenceNumber());
						//System.out.println("ack : " + cumulative_ack);
						
						if ( packet.getSequenceNumber() == cumulative_ack + 1 ) {
							cumulative_ack = packet.getSequenceNumber();
							//System.out.println("ack in: " + cumulative_ack);

//							if(cumulative_ack==27){
//								Thread.sleep(3000);
//							}
						}
						
//						if ( cumulative_ack == packet_no - 1 ) {
//							System.out.println("completed");
//							break;
//						}
					}
					
					
					
					while(AckQueue.isEmpty()==false){
						TCPPacket rcvd_packet = AckQueue.peek();
						AckQueue.remove();
						
						if(cumulative_ack < rcvd_packet.getSequenceNumber()){
							cumulative_ack = rcvd_packet.getSequenceNumber();
						}
						
						TCPPacket ack_packet = new TCPPacket((short) s.getPort(),rcvd_packet.getSourcePort(),
								0,cumulative_ack,(byte)(1<<4), (short) 1000);
						
						oos.writeObject(ack_packet);
					}
					
					
					if ( cumulative_ack == packet_no - 1 ) {
						

						System.out.println("Closing this connection : " + s);
						s.close();
						System.out.println("Connection closed");
						break;
					}
					
					

				}
			} catch (EOFException e) {
				System.out.println("EOF reached");
			}
			
			byte[] recieveBytes = new byte[ packet_no ];
			
			int i = 0;
			
			try {

				for ( i = 0; i < packet_no; i++ ) {
					recieveBytes[ i ] = dataStorer.get(i);
				}
				
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println(e.getCause());
				e.printStackTrace();
			}
			
			
			try ( FileOutputStream fos = new FileOutputStream("Received_file.txt") ) {
				
				fos.write(recieveBytes, 0, recieveBytes.length);
				System.out.println("Data has been written to the file.");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			scn.close();
			dis.close();
			dos.close();
			oos.close();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
