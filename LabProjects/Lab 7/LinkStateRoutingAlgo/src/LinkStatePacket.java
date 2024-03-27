import java.io.*;
import java.util.TreeMap;

public class LinkStatePacket {
	String sender,receiver;
	TreeMap<String,Integer> neighbourList;
	
	public LinkStatePacket( String sender, String receiver, TreeMap< String, Integer > neighbourList ) {
		this.sender = sender;
		this.receiver = receiver;
		this.neighbourList = neighbourList;
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
