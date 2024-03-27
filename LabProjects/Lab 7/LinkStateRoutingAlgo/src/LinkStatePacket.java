import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class LinkStatePacket implements Serializable{
	String sender,receiver;
	List<Edge> EdgeList;
	
	public LinkStatePacket( String sender, String receiver, ArrayList<Edge> EdgeList ) {
		this.sender = sender;
		this.receiver = receiver;
		this.EdgeList = EdgeList;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Sender: ").append(sender).append("\n");
		sb.append("Receiver: ").append(receiver).append("\n");
		sb.append("Edges:\n");
		for (Edge edge : EdgeList) {
			sb.append(edge).append("\n");
		}
		return sb.toString();
	}
	
	// Serialize the object into a byte array
	public static byte[] serializeObject(Object obj) {
		try ( ByteArrayOutputStream bos = new ByteArrayOutputStream();
		      ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(obj);
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Deserialize the byte array into an object
	public static Object deserializeObject(byte[] data) {
		try ( ByteArrayInputStream bis = new ByteArrayInputStream(data);
		      ObjectInputStream ois = new ObjectInputStream(bis)) {
			return ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
