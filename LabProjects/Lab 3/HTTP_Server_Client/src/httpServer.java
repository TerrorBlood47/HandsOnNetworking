import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class httpServer {
    
    public static final int PORT = 4200;
    public static final String
    FILE_PATH = System.getProperty("user.dir") + File.separator
            +"src" + File.separator + "ServerStorage";
    
    public static void main( String[] args ) throws IOException {
        
        //Establishing the Server
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server Established");
        
        while(true){
            try{
                Socket socket = serverSocket.accept();
                System.out.println("Client Connected " + socket);
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                
                Thread t = new ClientHandler(socket,dis,dos);
                
                t.start();
                
            }catch (IOException e){
                System.out.println(e.getCause());
                e.printStackTrace();
            }
        }
        
        
    }
}