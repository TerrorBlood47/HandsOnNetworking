import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final String FILES_PATH = "./serverfiles/";
    private ServerSocket serverSocket;
    public static final int PORT = 3030;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            acceptConnections();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptConnections() throws IOException {
        while (true) {
            Socket clientSocket = serverSocket.accept();
            if (clientSocket.isConnected())
                new Thread(() -> {

                    clientConnection client = new clientConnection(clientSocket);
                    try {
                        client.sendFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }).start();
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}