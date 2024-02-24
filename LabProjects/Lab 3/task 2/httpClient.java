import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class httpClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter the URL of the file to download: ");
            String url = scanner.nextLine();
            System.out.print("Enter the filename to save: ");
            String filename = scanner.nextLine();

            downloadFile(url, filename);

            System.out.println("File downloaded successfully to " + filename);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(String urlString, String filename) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(filename)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            connection.disconnect();
        }
    }
}
