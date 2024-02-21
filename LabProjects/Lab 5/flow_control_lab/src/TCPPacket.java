import java.io.Serializable;

public class TCPPacket implements Serializable {
	// Fields
	private int sourcePort; //16bit
	private int destinationPort;//16bit
	private int sequenceNumber;//32bit
	private int acknowledgmentNumber;//32bit
	private int flags;//6bit
	private int window_size;//16 bit
	

	
	
	// Constructor
	
	
	public TCPPacket( int sourcePort, int destinationPort, int sequenceNumber, int acknowledgmentNumber, int flags, int window_size ) {
		this.sourcePort = sourcePort;
		this.destinationPort = destinationPort;
		this.sequenceNumber = sequenceNumber;
		this.acknowledgmentNumber = acknowledgmentNumber;
		this.flags = flags;
		this.window_size = window_size;
	}
	
	public static int getTCPPacketSize(){
		return (16+16+32+32+6+16   +8);
	}
	
	
	@Override
	public String toString() {
		return "TCPPacket{" +
				"sourcePort=" + sourcePort +
				", destinationPort=" + destinationPort +
				", sequenceNumber=" + sequenceNumber +
				", acknowledgmentNumber=" + acknowledgmentNumber +
				", flags=" + flags +
				", window_size=" + window_size +
				'}';
	}
	
	public int getWindow_size() {
		return window_size;
	}
	
	public void setWindow_size( int window_size ) {
		this.window_size = window_size;
	}
	
	public int getSourcePort() {
		return sourcePort;
	}
	
	public void setSourcePort( int sourcePort ) {
		this.sourcePort = sourcePort;
	}
	
	public int getDestinationPort() {
		return destinationPort;
	}
	
	public void setDestinationPort( int destinationPort ) {
		this.destinationPort = destinationPort;
	}
	
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	
	public void setSequenceNumber( int sequenceNumber ) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public long getAcknowledgmentNumber() {
		return acknowledgmentNumber;
	}
	
	public void setAcknowledgmentNumber( int acknowledgmentNumber ) {
		this.acknowledgmentNumber = acknowledgmentNumber;
	}
	
	public int getFlags() {
		return flags;
	}
	
	public void setFlags( int flags ) {
		this.flags = flags;
	}
	
	// Getters and setters (not shown for brevity)
}
