import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
    private static double balance = 1000.0;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Bank server is running...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            while (true) {
                String action = (String) in.readObject();
                if (action.equals("withdraw")) {
                    double amount = (Double) in.readObject();
                    if (amount > 0 && balance >= amount) {
                        balance -= amount;
                        out.writeObject("Withdrawal successful. Remaining balance: $" + balance);
                    } else {
                        out.writeObject("Invalid withdrawal amount or insufficient funds.");
                    }
                } else if (action.equals("getBalance")) {
                    out.writeObject(balance);
                } else if (action.equals("exit")) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
