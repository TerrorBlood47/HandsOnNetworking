import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;

public class clientConnection {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public clientConnection(Socket socket) {
        this.socket = socket;
        try {
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile() throws IOException {
        System.out.println("connection started");
        sendMenu();
        int index = getSelectedFileIndex();
        sendSelectedFile(index);
    }

    private int getSelectedFileIndex() throws IOException {
        String input = in.readUTF();
        return Integer.parseInt(input) - 1;
    }

    private void sendSelectedFile(int index) throws IOException {
        File[] filelist = new File(Server.FILES_PATH).listFiles();
        File file = filelist[index];

        List<String> lines = Files.readAllLines(file.toPath());
        String fileContent = String.join("\n", lines);
        out.writeUTF(fileContent);
    }

    private void sendMenu() throws IOException {
        String menu = "*Files*\n";
        File[] filelist = new File(Server.FILES_PATH).listFiles();
        out.writeUTF("" + filelist.length);

        for (int i = 0; i < filelist.length; i++) {
            menu += (i + 1) + ". " + filelist[i].getName() + "\n";
        }

        out.writeUTF(menu);

    }
}
