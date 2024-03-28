import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

public class A {
    private static final Map<String, Map<String, Integer>> G = new HashMap<>();
    private static final Map<String, Integer> nodeCosts = new HashMap<>();
    private static final Map<String, String> parentsMap = new HashMap<>();
    private static final Set<String> visited = new HashSet<>();
    private static final PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.cost));

    private static String curr = "A";
    private static final int TTL = 60;
    private static final int PORT = 9771;
    private static final int UPDATE_INTERVAL = 30000; // 30 seconds

    static class Node {
        String node;
        int cost;

        Node(String node, int cost) {
            this.node = node;
            this.cost = cost;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        initialStart();

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("[STARTING] Server is starting");

        new Thread(() -> updateGraph()).start();
        new Thread(() -> sendMessage()).start();

        while (true) {
            System.out.println("[LISTENING] Server is listening on port " + PORT);
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private static void sendMessage() {
        try {
            Thread.sleep(10000); // Wait for 10 seconds before sending initial messages
            while (true) {
                String message = getMessageToSend();
                sendToNeighbours(message);
                Thread.sleep(UPDATE_INTERVAL);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String getMessageToSend() {
        StringBuilder messageBuilder = new StringBuilder(curr + " " + TTL + "\n");
        for (Map.Entry<String, Integer> entry : G.get(curr).entrySet()) {
            messageBuilder.append(curr).append(" ").append(entry.getKey()).append(" ").append(entry.getValue()).append("@");
        }
        return messageBuilder.toString();
    }

    private static void sendToNeighbours(String message) {
        Map<String,Integer> ports = new HashMap<>();
        ports.put("A", 9771);
        ports.put("B", 9772);
        ports.put("C", 9773);
        ports.put("D", 9774);
        ports.put("E", 9775);
        ports.put("F", 9776);

        for (Map.Entry<String, Integer> entry : ports.entrySet()) {
            if (!entry.getKey().equals(curr)) {
                try (Socket socket = new Socket("localhost", entry.getValue());
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    out.println(message);
                } catch (IOException e) {
                    System.err.println(curr + " couldn't connect with neighbour " + entry.getKey() + " on port " + entry.getValue());
                }
            }
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            StringBuilder receivedMessage = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                receivedMessage.append(line).append("\n");
            }
            processReceivedMessage(receivedMessage.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processReceivedMessage(String message) {
        String[] lines = message.split("\n");
        String[] idTTL = lines[0].split("\\s+");
        String id = idTTL[0];
        int ttl = Integer.parseInt(idTTL[1]);
        if (id.equals(curr)) {
            return;
        }
        if (ttl <= 0) {
            return;
        }
        String data = lines[1];
        String[] entries = data.split("@");
        for (String entry : entries) {
            String[] parts = entry.split("\\s+");
            String src = parts[0];
            String dest = parts[1];
            int cost = Integer.parseInt(parts[2]);
            if (!G.containsKey(src)) {
                G.put(src, new HashMap<>());
            }
            if (!G.containsKey(dest)) {
                G.put(dest, new HashMap<>());
            }
            G.get(src).put(dest, cost);
            G.get(dest).put(src, cost);
        }
        System.out.println("\nG updated\n");
        dijkstra(curr);
        printCost();
    }

    private static void updateGraph() {
        try {
            Thread.sleep(10000); // Wait for 10 seconds before updating graph
            while (true) {
                Thread.sleep(UPDATE_INTERVAL);
                Random random = new Random();
                String node = String.valueOf((char) (random.nextInt(4) + 'A'));
                int cost = random.nextInt(50) + 1;
                if (!node.equals(curr)) {
                    G.get(curr).put(node, cost);
                    G.get(node).put(curr, cost);
                    System.out.println("Updating cost " + curr + " " + node + " " + cost);
                    dijkstra(curr);
                    printCost();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void initialStart() throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new FileReader("src/graph_text.txt"))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                String src = parts[0];
                String dest = parts[1];
                int cost = Integer.parseInt(parts[2]);
                if (!G.containsKey(src)) {
                    G.put(src, new HashMap<>());
                }
                if (!G.containsKey(dest)) {
                    G.put(dest, new HashMap<>());
                }
                G.get(src).put(dest, cost);
                G.get(dest).put(src, cost);
            }
        }
        System.out.println(G);
        dijkstra(curr);
        printCost();
    }

    private static void dijkstra(String startingNode) {
        visited.clear();
        nodeCosts.clear();
        parentsMap.clear();
        pq.clear();

        nodeCosts.put(startingNode, 0);
        pq.offer(new Node(startingNode, 0));

        while (!pq.isEmpty()) {
            Node node = pq.poll();
            String u = node.node;
            if (visited.contains(u)) {
                continue;
            }
            visited.add(u);

            Map<String, Integer> neighbors = G.getOrDefault(u, new HashMap<>());
            for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
                String v = entry.getKey();
                int weight = entry.getValue();
                if (!visited.contains(v) && nodeCosts.getOrDefault(v, Integer.MAX_VALUE) > nodeCosts.get(u) + weight) {
                    nodeCosts.put(v, nodeCosts.get(u) + weight);
                    parentsMap.put(v, u);
                    pq.offer(new Node(v, nodeCosts.get(v)));
                }
            }
        }
    }

    private static void printCost() {
        System.out.println("\nNew Table for " + curr + ":\n");
        for (Map.Entry<String, Integer> entry : nodeCosts.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        System.out.println();
        printShortestPaths();
    }

    private static void printShortestPaths() {
        System.out.println("Path:\n");
        for (String destNode : G.keySet()) {
            if (!destNode.equals(curr)) {
                String path = String.join(" -> ", shortestPath(destNode));
                System.out.println("Shortest path from " + curr + " to " + destNode + ": " + path + " | Distance: " + nodeCosts.get(destNode));
            }
        }
        System.out.println();
    }

    private static String[] shortestPath(String destNode) {
        String[] path = new String[100]; // Assuming maximum path length
        int index = 0;
        String node = destNode;
        while (node != null && parentsMap.containsKey(node)) {
            path[index++] = node;
            node = parentsMap.get(node);
        }
        path[index++] = curr;
        String[] result = new String[index];
        for (int i = 0; i < index; i++) {
            result[i] = path[index - i - 1];
        }
        return result;
    }
}
