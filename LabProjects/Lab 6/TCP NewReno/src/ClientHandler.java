import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.util.PriorityQueue;
import java.util.Queue;

// ClientHandler class
public class ClientHandler extends Thread {
	final ServerSocket ss;
	final DataInputStream dis;
	final DataOutputStream dos;
	final Socket s;
	
	final ObjectOutputStream oos;
	
	final ObjectInputStream ois;
	
	private static final int SlowStart = 11111111;
	
	private static final int CongestionAvoidance = 22222222;
	
	private static final int FastRecovery = 44444444;
	
	
	
	private static int cwnd = 1;
	
	private static int rwnd = 1;
	
	private static int window_size = cwnd;
	private static int ssthresh = 3;
	
	
	public ClientHandler( ServerSocket ss, Socket s, DataInputStream dis, DataOutputStream dos, ObjectOutputStream oos, ObjectInputStream ois ) throws IOException {
		this.ss = ss;
		this.s = s;
		this.dis = dis;
		this.dos = dos;
		this.oos = oos;
		this.ois = ois;
	}
	
	
	@Override
	public void run() {
		
		
		String received;
		
		Queue< Integer > tempQueueOfUnAckSeqNum = new PriorityQueue<>();///does not allow duplicate
		
		long timeOut = 1000000000; //1 second
		
		//turning the file data into bits
		
		String filepath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "a.txt";
		File file = new File(filepath);
		
		byte[] fileBytes = null;
		
		
		try {
			fileBytes = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
		}
		
		boolean[] ackReceived = new boolean[ fileBytes.length ];
		
		//handshaking
		while ( true ) {
			try {
				TCPPacket SYN_packet_rcvd = (TCPPacket) ois.readObject();
				System.out.println(SYN_packet_rcvd);
				
				if ( SYN_packet_rcvd.isSYN() ) {
					TCPPacket SYNACK_packet = new TCPPacket(Server.PORT, (short) s.getPort(), - 1,
							SYN_packet_rcvd.getAcknowledgmentNumber() + 1, (byte) ( 1 << 4 ), (short) fileBytes.length);
					oos.writeObject(SYNACK_packet);
					
					TCPPacket SYNACK_ACK_packet_rcvd = (TCPPacket) ois.readObject();
					
					if ( SYNACK_ACK_packet_rcvd.isACK() ) break; //start transmission of file
				}
				
			} catch (IOException e) {
				System.out.println(e.getCause());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println(e.getCause());
				e.printStackTrace();
			}
		}
		int i = 0;
		
		int currState = SlowStart;
		int prevCurrState = currState;
		
		Pair< Integer, Integer > lastPacketNotFoundAck = new Pair<>(0, 0);
		
		
		
		while ( true ) {
			
			System.out.println();
			System.out.println(this.window_size);
			
			this.window_size = Math.min(this.window_size, fileBytes.length - i);
			
			
			try {
				
				if ( this.window_size == 0 ) {
					throw new EOFException();
				}
				
				
				long start_time = System.nanoTime();
				
				//s.setSoTimeout(timeOut);
				
				if ( currState == SlowStart ) {
					
					
					System.out.println(".....[Slow Start].....");
					for ( int j = 0; j < this.window_size; j++ ) {
						
						if ( i == file.length() ) {
							break;
						}
						assert fileBytes != null;
						byte byteNum = fileBytes[ i ];
						
						short srcPort = (short) this.ss.getLocalPort();
						short destPort = (short) this.s.getPort();
						byte flag = 0; //0->seq
						
						TCPPacket snd_packet = new TCPPacket(srcPort, destPort, i, 0, flag, (short) window_size);
						snd_packet.setData(byteNum);
						
						//sending header
						oos.writeObject(snd_packet);
						
						
						System.out.println("sent byte no " + ( i ));
						i++;
					}
					
					
					//go to Congestion Avoidance
					if ( cwnd > ssthresh ) {
						System.out.println("==================[cwnd > ssthresh] go to Congestion Avoidance=================");
						prevCurrState = SlowStart;
						currState = CongestionAvoidance;
						continue;
					}
					
					
					cwnd = cwnd * 2;
					
					
				} else if ( currState == CongestionAvoidance ) {
					
					System.out.println(".....[Congestion Avoidance].....");
					for ( int j = 0; j < this.window_size; j++ ) {
						
						if ( i == file.length() ) {
							break;
						}
						assert fileBytes != null;
						byte byteNum = fileBytes[ i ];
						
						short srcPort = (short) this.ss.getLocalPort();
						short destPort = (short) this.s.getPort();
						byte flag = 0; // 0->seq
						
						TCPPacket snd_packet = new TCPPacket(srcPort, destPort, i, 0, flag, (short) this.window_size);
						snd_packet.setData(byteNum);
						//sending header
						oos.writeObject(snd_packet);
						
						
						System.out.println("sent byte no " + ( i ));
						i++;
					}
					
					
					cwnd = cwnd + 1;
					
				} else if ( currState == FastRecovery ) {
					
					//Selective retransmit
					
					short temp_window_size = (short) window_size;
					
					window_size = tempQueueOfUnAckSeqNum.size();
					
					System.out.println(".....[Fast Recovery].....");
					while ( tempQueueOfUnAckSeqNum.isEmpty()==false ) {
						
						int index = tempQueueOfUnAckSeqNum.peek();
						tempQueueOfUnAckSeqNum.remove();
						
						assert fileBytes != null;
						byte byteNum = fileBytes[ index ];
						
						short srcPort = (short) this.ss.getLocalPort();
						short destPort = (short) this.s.getPort();
						byte flag = 0;
						
						TCPPacket snd_packet = new TCPPacket(srcPort, destPort, index, 0, flag, (short) this.window_size);
						snd_packet.setData(byteNum);
						//sending header
						oos.writeObject(snd_packet);
						
						System.out.println("sent byte no " + ( index ));
						
					}
					window_size = temp_window_size;
					currState = prevCurrState;
					
				} else {
					System.out.println("Unknown State");
					break;
				}
				
				
				///======================================================================================///
				
				
				
				
				int ack = 0;
				
				
				
				
				//receive acknowledgement
				
				for ( int j = 0; j < window_size; j++ ) {
					TCPPacket receivedPacket = (TCPPacket) ois.readObject();
					rwnd = receivedPacket.getReceive_window();
					
					if ( lastPacketNotFoundAck.first == ack ) {
						lastPacketNotFoundAck.second += 1;
					} else {
						lastPacketNotFoundAck.first = ack;
						lastPacketNotFoundAck.second = 1;
					}
					
					ack = Math.max(ack, receivedPacket.getAcknowledgmentNumber());
					System.out.println("received byte acknowledgement : " + ack);
					
					//3 duplicate acknowledgement found
					if ( lastPacketNotFoundAck.second >= 3 ) {
						System.out.println("=============================== 3 Duplicate Acknowledgement =============================");
						System.out.println("noted ..... ");
						
						tempQueueOfUnAckSeqNum.add(ack - 1);
						tempQueueOfUnAckSeqNum.add(ack - 2);
						tempQueueOfUnAckSeqNum.add(ack - 3);
					}
				}
				
				
				long ackReceivingTime = System.nanoTime();
				
				long delay = ackReceivingTime - start_time;
				
				System.out.println("delay : " + delay + " ns");
				
				
				System.out.println("EWMA timeout: " + timeOut + " ns");
				
				
				if ( delay > timeOut ) {
					throw new SocketTimeoutException("Packet Loss Occurred");
				}
				
				timeOut = EWMA.getTimeOutInterval(delay);
				
				
				this.window_size = Math.min(cwnd, rwnd);
				
				
				if ( ack >= file.length() ) {
					
					System.out.println("whole file is received by the receiver");
					break;
				}
				
				if(tempQueueOfUnAckSeqNum.isEmpty()==false){
					prevCurrState = currState;
					currState = FastRecovery;
				}
				
				
				System.out.println("here i = " + i);
				
				
			} catch (SocketTimeoutException e) {
				
				System.out.println("=========================did not receive anything during timeOutInterval=========================");
				System.out.println("need to go to [Slow Start Phase] ....... ");
				i = i - window_size;
				prevCurrState = currState;
				currState = SlowStart;
				ssthresh = Math.max(cwnd / 2, 1);
				cwnd = 1;
				this.window_size = cwnd;
				continue;
			} catch (EOFException e) {
				System.out.println("Whole file has been received by the client");
				break;
			} catch (SocketException e) {
				System.out.println("Whole file has been received by the client");
				break;
			} catch (ClassNotFoundException | IOException e) {
				System.out.println(e.getCause());
				e.printStackTrace();
			}
		}
		
		
	}
}
		