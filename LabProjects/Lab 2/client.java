import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class client {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Scanner scanner;

    public client() {
        try {
            socket = new Socket("127.0.0.1", Server.PORT);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            scanner = new Scanner(System.in);

            getFile();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFile() throws IOException {
        String filesLen = in.readUTF();
        int maxFiles = Integer.parseInt(filesLen);
        String menu = in.readUTF();
        System.out.println(menu);

        int userSelection = 1;
        boolean isSelectionCorrect = false;
        while (!isSelectionCorrect) {
            System.out.print("Select a file number: ");
            userSelection = scanner.nextInt();
            isSelectionCorrect = userSelection > 0 && userSelection <= maxFiles;
        }
        out.writeUTF("" + userSelection);
        String fileContent = in.readUTF();

        System.out.println(" -- FILE START --");
        System.out.println(fileContent);
        System.out.println(" -- FILE END --");
    }

    public static void main(String[] args) {
        new client();
    }
}