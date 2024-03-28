import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class NodeRouter_A {

	private static TreeMap< String, Integer > nodeIdentifier = new TreeMap<>();

	static {
		nodeIdentifier.put("A", 0);
		nodeIdentifier.put("B", 1);
		nodeIdentifier.put("C", 2);
		nodeIdentifier.put("D", 3);
		nodeIdentifier.put("E", 4);
		nodeIdentifier.put("F", 5);
	}

	public static String ROUTER_NAME = "A";
	public static int PORT = 1300;

	public static String IP = "localhost";

	public static TreeMap< String, Integer > neighboursList = new TreeMap<>();

	private static Integer Graph[][] = new Integer[ 6 ][ 6 ];
	private static int parent[] = new int[ 6 ];

	static {
		neighboursList.put("B", 20);
		neighboursList.put("D", 10);

		for ( int i = 0; i < Graph.length; i++ ) {
			for ( int j = 0; j < Graph[ i ].length; j++ ) {
				Graph[ i ][ j ] = Integer.MAX_VALUE;
			}
		}

		int u = 0; //for A

		for ( Map.Entry< String, Integer > entry : neighboursList.entrySet() ) {
			int v = nodeIdentifier.get(entry.getKey());
			int cost = entry.getValue();

			Graph[ u ][ v ] = cost;
			Graph[ v ][ u ] = cost;
		}

		for ( int i = 0; i < parent.length; i++ ) {
			parent[ i ] = - 1;
		}
	}


	public static void main( String[] args ) {

		Scanner sc = new Scanner(System.in);

		try {
			calculateDijkstra();
			printAllPaths();
			DatagramSocket routing_server = new DatagramSocket(PORT);
			System.out.println("[ESTABLISHED] Router" + ROUTER_NAME + " Established");



				//update/send thread
				new Thread() {

					static {
						System.out.println("[UPDATING THREAD ACTIVATED]");
					}

					public void run() {

						while ( true ) {

							synchronized (this) {

								String input = sc.nextLine();

								System.out.println(input);

								String words[] = input.split(" ");

								if ( words[ 0 ].equals("printpath") ) {
									printPath(words[ 1 ]);
								} else if ( words[ 0 ].equals("updateEdge") ) {
									String src = words[ 1 ];
									String dest = words[ 2 ];
									Integer cost = Integer.parseInt(words[ 3 ]);

									if(ROUTER_NAME.equals(src) && neighboursList.get(dest)!=cost) {
										neighboursList.put(dest, cost);

										int u = nodeIdentifier.get(src);
										int v = nodeIdentifier.get(dest);

										Graph[ u ][ v ] = cost;
										Graph[ v ][ u ] = cost;

										ArrayList< Edge > new_edge_list = getNewEdgeList();

										try {
											sendToAllNeighbours(routing_server, new_edge_list);
										} catch (IOException e) {
											e.printStackTrace();
											System.out.println(e.getCause());
										}

										printPath("A");
										printPath("B");
										printPath("C");
										printPath("D");
									}

								} else if ( words[ 0 ].equals("send") || words[ 0 ].equals("start") ) {

									ArrayList< Edge > new_edge_list = new ArrayList<>();

									for ( Map.Entry< String, Integer > entry : neighboursList.entrySet() ) {
										new_edge_list.add(new Edge(ROUTER_NAME, entry.getKey(), entry.getValue()));
									}

									try {
										sendToAllNeighbours(routing_server, new_edge_list);
									} catch (IOException e) {
										e.printStackTrace();
										System.out.println(e.getCause());
									}

									printPath("A");
									printPath("B");
									printPath("C");
									printPath("D");
								}
							}
						}
					}

				}.start();

				//listening thread
				new Thread() {

					static {
						System.out.println("[LISTENING THREAD ACTIVATED]");
					}

					public void run() {

						while(true) {

							synchronized (this) {

								byte[] receiveData = new byte[ 4 * 1024 ];
								DatagramPacket datagramPacket = new DatagramPacket(receiveData, receiveData.length);
								try {

									routing_server.receive(datagramPacket);
									System.out.println("[RECEIVED PACKET]");

									LinkStatePacket packet = (LinkStatePacket) LinkStatePacket.deserializeObject(datagramPacket.getData());

									System.out.println(packet);

									boolean graphUPDATED = updateGraph(packet);

									boolean networkChanged = false;

									if ( graphUPDATED ) {
										networkChanged = calculateDijkstra();
									}

									if(networkChanged){
										networkChanged = false;

										System.out.println("<<<<<<<<< network changed >>>>>>>>>>>");

										ArrayList< Edge > new_edge_list = getNewEdgeList();

										try {
											sendToAllNeighbours(routing_server, new_edge_list);
										} catch (IOException e) {
											e.printStackTrace();
											System.out.println(e.getCause());
										}

									}

									printPath("A");
									printPath("B");
									printPath("C");
									printPath("D");


								} catch (IOException e) {
									e.printStackTrace();
									System.out.println(e.getCause());
								}
							}
						}
					}
				}.start();



		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getCause());
		}
	}

	private static synchronized ArrayList<Edge> getNewEdgeList() {

		ArrayList<Edge> new_Edge_list = new ArrayList<>();

		for(int i=0; i<Graph.length; i++){
			for(int j=0; j<Graph[i].length; j++){

				if(i!=j && Graph[i][j] != Integer.MAX_VALUE){
					new_Edge_list.add(new Edge(getKeyByValue(nodeIdentifier,i),getKeyByValue(nodeIdentifier,j),Graph[i][j]));
				}
			}
		}

		return new_Edge_list;
	}


	private static synchronized void sendToAllNeighbours( DatagramSocket socket, ArrayList< Edge > EdgeList ) throws IOException {
		System.out.println("[SENDING LS_PACKET TO ALL NEIGHBOURS]");

		for ( Map.Entry< String, Integer > entry : neighboursList.entrySet() ) {
			LinkStatePacket snd_LS_packet = new LinkStatePacket(ROUTER_NAME, entry.getKey(), EdgeList);

			byte[] serializedData = LinkStatePacket.serializeObject(snd_LS_packet);
			System.out.println("byte len: " + serializedData.length);

			DatagramPacket snd_packet = new DatagramPacket(serializedData, serializedData.length,
					InetAddress.getByName(IP), Utils.getPort(entry.getKey()));

			socket.send(snd_packet);
		}
	}


	private static void printPath(String dest) {
		System.out.println("Printing Path from src " + ROUTER_NAME + " to dest " + dest);
		int d = nodeIdentifier.get(dest);
		StringBuilder pathBuilder = new StringBuilder();
		int totalDistance = 0;
		while (d != -1) {
			String routerName = getKeyByValue(nodeIdentifier, d);
			pathBuilder.insert(0, " -> " + routerName);
			int parentIndex = parent[d];
			if (parentIndex != -1) {
				totalDistance += Graph[d][parentIndex]; // Assuming graph is a symmetric matrix
			}
			d = parentIndex;
		}
		System.out.println("Path:" + pathBuilder.substring(4) + ", Total Distance: " + totalDistance);
	}


	private static void printAllPaths() {
		printPath("A");
		printPath("B");
		printPath("C");
		printPath("D");
		printPath("E");
		printPath("F");
		System.out.println(); // Add a newline for better readability
	}

	private static synchronized  < K, V > K getKeyByValue( TreeMap< K, V > map, V value ) {
		for ( Map.Entry< K, V > entry : map.entrySet() ) {
			if ( value.equals(entry.getValue()) ) {
				return entry.getKey();
			}
		}
		return null;
	}

	private static synchronized boolean calculateDijkstra() {
		int n = Graph.length;
		int dist[] = new int[n];
		boolean[] visited = new boolean[n];
		int[] newParent = new int[n];

		boolean networkUpdated = false;

		for (int i = 0; i < n; i++) {
			dist[i] = Integer.MAX_VALUE;
			visited[i] = false;
			newParent[i] = -1;
		}

		dist[nodeIdentifier.get(ROUTER_NAME)] = 0; // Set distance to itself as 0

		for (int i = 0; i < n; i++) {
			int u = nextMinIndex(dist, visited);
			if (u == -1) break; // No reachable nodes left
			visited[u] = true;

			for (int v = 0; v < n; v++) {
				if (!visited[v] && Graph[u][v] != Integer.MAX_VALUE && dist[u] + Graph[u][v] < dist[v]) {
					dist[v] = dist[u] + Graph[u][v];
					newParent[v] = u;
				}
			}
		}

		for (int i = 0; i < n; i++) {
			if (newParent[i] != parent[i]) {
				parent[i] = newParent[i];
				networkUpdated = true;
			}
		}

		return networkUpdated;
	}

	private static synchronized int nextMinIndex( int[] dist, boolean[] visited ) {

		int minValue = Integer.MAX_VALUE;
		int minIndex = - 1;

		for ( int i = 0; i < dist.length; i++ ) {

			if ( ! visited[ i ] && dist[ i ] < minValue ) {
				minValue = dist[ i ];
				minIndex = i;
			}

		}
		return minIndex;
	}

	private static  synchronized Boolean updateGraph( LinkStatePacket packet ) {
		boolean graphUpdated = false;

		String sender = packet.sender;

		for ( Edge edge : packet.EdgeList ) {

			int u = nodeIdentifier.get(edge.start);
			int v = nodeIdentifier.get(edge.end);
			int cost = edge.weight;

			if ( edge.start.equals(ROUTER_NAME) ) {
				if ( Graph[ u ][ v ] != cost ) {
					Graph[ u ][ v ] = cost;
					Graph[ v ][ u ] = cost;
					graphUpdated = true;
				}
			} else if ( edge.end.equals(ROUTER_NAME) ) {
				Graph[ u ][ v ] = cost;
				Graph[ v ][ u ] = cost;
				graphUpdated = true;
			}


		}

		return graphUpdated;
	}

	private static <K, V> K getKeyByValue(Map<K, V> map, V value) {
		for (Map.Entry<K, V> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}
}
