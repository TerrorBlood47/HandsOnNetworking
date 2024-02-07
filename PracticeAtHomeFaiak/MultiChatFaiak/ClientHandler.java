import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
	Thread t;
	Socket socket;
	
	ObjectInputStream ois ;
	ObjectOutputStream oos;
	
	ArrayList<ClientHandler> clientHandlers = Server.clientHandlers;
	String username;
	ClientHandler( Socket socket, String username){
		this.socket = socket;
		this.username = username;
		
		try {
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
		}
		
		t = new Thread(this, username);
		t.start();
	}
	
	@Override
	public void run() {
		
		
		try {
			while ( true ) {
				
				Object inputobject = ois.readObject();
				if ( inputobject != null ) {
					System.out.println(inputobject + " received");
				}
				String send = null;
				
				if ( inputobject instanceof String && inputobject.equals("exit") ) {
					System.out.println("exiting");
					break;
				}
				
				if ( inputobject instanceof Number ) {
					Integer n = ( (Number) inputobject ).intValue();
					
					send = isPrime(n);
				} else if ( inputobject instanceof String ) {
					send = inputobject.toString().toUpperCase();
				} else {
					send = inputobject.toString() + " : normal message ";
				}
				
				send = new String(username + " : " + send);
				//oos.writeObject(send);
				
				for(ClientHandler clientHandler : clientHandlers){
					clientHandler.oos.writeObject(send);
				}
				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getCause());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getCause());
		}
		try {
			oos.close();
			ois.close();
			socket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	static String isPrime(Integer n){
		
		int i=2;
		
		for(i=2; i<n;i++){
			if(n%i==0){
				return n.toString() + " is not a Prime number";
			}
		}
		
		return n.toString() + " is a prime number";
	}
}
