import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;

public class httpServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new FileHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

        System.out.println("HTTP server listening on port 8080");
    }

    static class FileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();

            if (requestMethod.equalsIgnoreCase("GET")) {
                // Extract the file path from the URL
                String filepath = exchange.getRequestURI().getPath().substring(1);

                File file = new File(filepath);

                if (file.exists()) {
                    // Send the file with appropriate HTTP headers
                    exchange.sendResponseHeaders(200, file.length());

                    try (OutputStream os = exchange.getResponseBody();
                         FileInputStream fis = new FileInputStream(file)) {

                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = fis.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    }
                } else {
                    exchange.sendResponseHeaders(404, 0);
                }

            } else if (requestMethod.equalsIgnoreCase("POST")) {
                // Handle POST request to save the file (not implemented in this example)
                exchange.sendResponseHeaders(501, 0); // Not Implemented
            }

            exchange.close();
        }
    }
}
