import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class client {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);

        try (Socket socket = new Socket("localhost", 8080);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            System.out.println("Connected to the bank server.");

            while (true) {
                System.out.println("\nChoose an action:");
                System.out.println("1. Withdraw");
                System.out.println("2. Check Balance");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // consume the newline character

                switch (choice) {
                    case 1:
                        System.out.print("Enter the withdrawal amount: $");
                        double withdrawalAmount = scanner.nextDouble();
                        out.writeObject("withdraw");
                        out.writeObject(withdrawalAmount);

                        String withdrawalResponse = (String) in.readObject();
                        System.out.println(withdrawalResponse);
                        break;

                    case 2:
                        out.writeObject("getBalance");
                        double balance = (Double) in.readObject();
                        System.out.println("Your current balance: $" + balance);
                        break;

                    case 3:
                        out.writeObject("exit");
                        System.out.println("Exiting the bank application. Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
            }
        }
    }
}
