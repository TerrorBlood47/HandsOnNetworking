import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class NodeRouter_A{
	
	private static TreeMap<String,Integer> nodeIdentifier = new TreeMap<>();
	
	static {
		nodeIdentifier.put("A",0);
		nodeIdentifier.put("B",1);
		nodeIdentifier.put("C",2);
		nodeIdentifier.put("D",3);
	}
	
	public static String ROUTER_NAME = "A";
	public static int PORT = 1300;
	
	public String IP = "localhost";
	
	public static TreeMap<String, Integer> neighboursList = new TreeMap<>();
	
	private static Integer Graph[][] = new Integer[4][4];
	private static int parent[] = new int[4];
	
	static {
		neighboursList.put("B", 20);
		neighboursList.put("D", 10);
		
		for(int i=0; i<Graph.length; i++){
			for(int j=0; j<Graph[i].length; j++){
				Graph[i][j] = Integer.MAX_VALUE;
			}
		}
		
		int u = 0; //for A
		
		for(Map.Entry<String,Integer> entry: neighboursList.entrySet()){
			int v = nodeIdentifier.get(entry.getKey());
			int cost = entry.getValue();
			
			Graph[u][v] = cost;
			Graph[v][u] = cost;
		}
		
		for(int i=0; i<parent.length; i++){
			parent[i] = -1;
		}
	}
	
	
	public static void main( String[] args ) {
		
		
		
		Scanner sc = new Scanner(System.in);
		
		
		try{
			DatagramSocket routing_server = new DatagramSocket(PORT);
			System.out.println("[ESTABLISHED] Router A Established");
			
			//listening thread
			new Thread() {
				public void run() {
					byte[] receiveData = new byte[4*1024];
					DatagramPacket datagramPacket = new DatagramPacket(receiveData,receiveData.length);
					try {
						routing_server.receive(datagramPacket);
						System.out.println("[RECEIVED PACKET]");
						
						LinkStatePacket packet = (LinkStatePacket) LinkStatePacket.deserializeObject(datagramPacket.getData());
						
						boolean graphUPDATED = updateGraph(packet);
						
						if(graphUPDATED){
							calculateDijkstra();
						}
						
						
						printPath("B");
						printPath("C");
						
						
					} catch (IOException e) {
						e.printStackTrace();
						System.out.println(e.getCause());
					}
				}
			}.start();
			
			//update thread
			new Thread() {
				
				public void run() {
					while ( true ) {
						String input = sc.nextLine();
						
						String words[] = input.split(" ");
						
						if(words[0].equals("printpath")){
						
						}
						else{
						
						}
					}
				}
			}.start();
			
			
			
		}
		catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getCause());
		}
	}
	
	private static void printPath(String dest) {
		System.out.println("Printing Path from src" + ROUTER_NAME + " to dest " + dest);
		int d = nodeIdentifier.get(dest);
		
		while(true){
			
			if(d==-1) break;
			
			String routerName = getKeyByValue(nodeIdentifier, d);
			System.out.println(routerName);
			
			d = parent[ d ];
		}
	}
	
	private static <K, V> K getKeyByValue(TreeMap<K, V> map, V value) {
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	private static void calculateDijkstra( ) {
		int n = Graph.length;
		int dist[] = new int[n];
		boolean[] visited = new boolean[n];
		
		for(int i=0; i<n; i++) {
			dist[i] = Integer.MAX_VALUE;
			visited[i] = false;
		}
		
		dist[0] = 0; //for src
		
		for(int i=0; i<n; i++){
			int u = nextMinIndex(dist,visited);
			
			for(int v=0; v<n; v++){
				if(u==v) continue;
				
				if(!visited[v] && Graph[u][v]!=Integer.MAX_VALUE
						&& (dist[u] + Graph[u][v] < dist[v])){
					dist[v] = dist[u] + Graph[u][v];
					parent[v] = u;
				}
			}
		}
		
	}
	
	private static int nextMinIndex( int[] dist, boolean[] visited ) {
		
		int minValue = Integer.MAX_VALUE;
		int minIndex = -1;
		
		for(int i=0; i<dist.length; i++){
			
			if(!visited[i] && dist[i]<minValue){
				minValue = dist[i];
				minIndex = i;
			}
		
		}
		return minIndex;
	}
	
	private static Boolean updateGraph( LinkStatePacket packet ) {
		boolean graphUpdated = false;
		
		String sender = packet.sender;
		int u = nodeIdentifier.get(sender);
		
		for( Map.Entry< String, Integer > entry: packet.neighbourList.entrySet() ){
			int v = nodeIdentifier.get(entry.getKey());
			int cost = entry.getValue();
			
			if(Graph[u][v]!=cost){
				Graph[u][v] = cost;
				Graph[v][u] = cost;
				graphUpdated = true;
			}
		}
		
		return graphUpdated;
	}
}
