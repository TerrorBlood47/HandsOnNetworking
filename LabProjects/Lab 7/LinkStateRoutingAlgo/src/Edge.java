import java.io.Serializable;

public class Edge implements Serializable {
	public String start,end;
	public int weight;
	
	public Edge( String start, String end, int weight ) {
		this.start = start;
		this.end = end;
		this.weight = weight;
	}
	
	@Override
	public String toString() {
		return "[" + start + " -> " + end + ", weight: " + weight + "]";
	}
}
