
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;



public class Server {
	public static void main( String[] args ) {
		
		try {
			//create the server socket
			ServerSocket serverSocket = new ServerSocket(15000);
			System.out.println("Chat.Server connected");
			
			//connect to a client socket
			Socket socket = serverSocket.accept();
			System.out.println("Client connected");
			
			//for transmitting and receiving data
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			
			while ( true ){
				
				//reading messages sent by client
				String in = (String) ois.readObject();
				System.out.println(in);
				
				String words[] = in.split(" ");
				
				String send = null;
				
				//implementing the code logic done at the server
				if(words[0].toLowerCase().equals("prime")){
					Integer n = Integer.parseInt(words[1]);
					
					send = isPrime(n);
				}
				else if ( words[0].toLowerCase().equals("palindrome")){
					
					send = words[1]+ " " + isPalindrome(words[1]);
				}
				else{
					send = in.toUpperCase();
				}
				
				//sending messages to client
				oos.writeObject(send);
			}
			
		} catch (IOException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	static  String isPrime(Integer n){
		
		if(n==1){
			return n.toString() + " is a prime";
		}
		
		for(int i=2; i<n; i++){
			if(n%i==0){
				return n.toString() + " is not a prime";
			}
		}
		return n.toString() + " is a prime";
	}
	
	static String isPalindrome(String string){
		
		int i = 0;
		int j = string.length()-1;
		
		while(i<=j){
			if(string.charAt(i) != string.charAt(j)){
				return "not a palindrome";
			}
			
			i++;
			j--;
		}
		
		return "is a palindrome !!";
	}
	
}