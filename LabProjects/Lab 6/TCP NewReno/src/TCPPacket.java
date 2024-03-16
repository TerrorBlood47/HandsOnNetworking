import java.io.Serializable;

public class TCPPacket implements Serializable,Comparable<TCPPacket> {
	// Fields
	
	private boolean CWR,ECE,URG,ACK,PSH,RST,SYN,FIN;
	private short sourcePort; //16bit
	private short destinationPort;//16bit
	private int sequenceNumber;//32bit
	private int acknowledgmentNumber;//32bit
	private byte flags;//6bit
	private short receive_window;//16 bit
	
	private byte header_length;//4 bit
	
	private short urgent_data_pointer;//16 bit
	
	private short checksum;//16 bit
	
	private byte data;
	
	
	// Constructor
	
	
	public TCPPacket( short sourcePort, short destinationPort, int sequenceNumber, int acknowledgmentNumber, byte flags ) {
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
		this.sequenceNumber = sequenceNumber;
		this.acknowledgmentNumber = acknowledgmentNumber;
		this.flags = flags;
		
		this.header_length = 1+1+2+2+1+1;
		this.checksum = CreateCheckSum();
		populateFlags();
	}
	
	public TCPPacket( short sourcePort, short destinationPort, int sequenceNumber, int acknowledgmentNumber, byte flags, short receive_window ) {
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
		this.sequenceNumber = sequenceNumber;
		this.acknowledgmentNumber = acknowledgmentNumber;
		this.flags = flags;
		this.receive_window = receive_window;
		
		this.header_length = 1+1+2+2+1+1;
		this.checksum = CreateCheckSum();
		populateFlags();
	}
	

	
	private void populateFlags() {
		
		if((flags & (1<<0))!=0){
			FIN = true;
		}
		
		if((flags & (1<<1))!=0){
			SYN = true;
		}
		
		if((flags & (1<<2))!=0){
			RST = true;
		}
		
		if((flags & (1<<3))!=0){
			PSH = true;
		}
		
		if((flags & (1<<4))!=0){
			ACK = true;
		}
		
		if((flags & (1<<5))!=0){
			URG = true;
		}
		
		if((flags & (1<<6))!=0){
			ECE = true;
		}
		
		if((flags & (1<<7))!=0){
			CWR = true;
		}
	}
	
	private short CreateCheckSum() {
		short chksum = (short) (sourcePort + destinationPort + (short)(sequenceNumber >> 16) + (short) sequenceNumber +
						(short) (acknowledgmentNumber>>16) + (short) acknowledgmentNumber + (short)(header_length<<12 + flags)
						+ receive_window);
		
		chksum = (short) ~chksum;
		
		return chksum;
	}
	
	public static int getTCPPacketSize(){
		return (16+16+32+32+16+16+16+16   +8);
	}
	
	
	@Override
	public String toString() {
		return "TCPPacket{" +
				"sourcePort=" + sourcePort +
				", destinationPort=" + destinationPort +
				", sequenceNumber=" + sequenceNumber +
				", acknowledgmentNumber=" + acknowledgmentNumber +
				", flags=" + flags +
				", window_size=" + receive_window +
				'}';
	}
	
	public short getSourcePort() {
		return sourcePort;
	}
	
	public void setSourcePort( short sourcePort ) {
		this.sourcePort = sourcePort;
	}
	
	public short getDestinationPort() {
		return destinationPort;
	}
	
	public void setDestinationPort( short destinationPort ) {
		this.destinationPort = destinationPort;
	}
	
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	
	public void setSequenceNumber( int sequenceNumber ) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public int getAcknowledgmentNumber() {
		return acknowledgmentNumber;
	}
	
	public void setAcknowledgmentNumber( int acknowledgmentNumber ) {
		this.acknowledgmentNumber = acknowledgmentNumber;
	}
	
	public byte getFlags() {
		return flags;
	}
	
	public void setFlags( byte flags ) {
		this.flags = flags;
		populateFlags();
	}
	
	public short getReceive_window() {
		return receive_window;
	}
	
	public void setReceive_window( short receive_window ) {
		this.receive_window = receive_window;
	}
	
	public short getChecksum() {
		return checksum;
	}
	
	public void setChecksum( short checksum ) {
		this.checksum = checksum;
	}
	
	public byte getData() {
		return data;
	}
	
	public void setData( byte data ) {
		this.data = data;
	}
	
	public boolean isCWR() {
		return CWR;
	}
	
	public boolean isECE() {
		return ECE;
	}
	
	public boolean isURG() {
		return URG;
	}
	
	public boolean isACK() {
		return ACK;
	}
	
	public boolean isPSH() {
		return PSH;
	}
	
	public boolean isRST() {
		return RST;
	}
	
	public boolean isSYN() {
		return SYN;
	}
	
	public boolean isFIN() {
		return FIN;
	}
	
	@Override
	public int compareTo( TCPPacket other ) {
		return Integer.compare(this.sequenceNumber,other.sequenceNumber);
	}
}
