
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class BankServer {
	public static void main( String[] args ) {
		
		String userID = "10";
		String userPassword = "1234";
		int userBalance = 1000;
		
		ArrayList<String> history = new ArrayList<>();
		
		
		try {
			ServerSocket serverSocket = new ServerSocket(15000);
			System.out.println("Chat.Server connected");
			
			Socket socket = serverSocket.accept();
			System.out.println("Client connected");
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			
			while(true){
				String in = (String) ois.readObject();
				System.out.println(in);
				
				String words[] = in.split(" ");
				
				String send = null;
				if(words[0].equals(userID) && words[1].equals(userPassword)){ //withdraw
					
					String mssg = "welcome";
					oos.writeObject(mssg);
					break;
				}else{
					String mssg = "wrong info";
					oos.writeObject(mssg);
				}
				
			}
			
			while ( true ){
				
				String in = (String) ois.readObject();
				System.out.println(in);

				int isError = (int) Math.random()%(10-1+1) + 1 ;

				
				if( ! history.isEmpty() )
				{
					if ( history.get(history.size() - 1).equals(in) ) {
						String mssg = "duplicate";
						oos.writeObject(mssg);
						continue;
					}
				}
				
				String words[] = in.split(" ");
				
				String send = null;
				if(words[0].toLowerCase().equals("1")){ //withdraw
					
					if(words.length == 1){
						send = "please enter amount";
					}else if(Integer.parseInt(words[0]) > userBalance){
						send = "not enough money";
					}else{
						send = Integer.parseInt(words[1]) + " taka withdrawn";
						userBalance -= Integer.parseInt(words[1]);
						history.add(in);
					}
				}
				else if ( words[0].toLowerCase().equals("2")){
					
					send = "balance is " + userBalance;
					history.add(in);
				}else if(words[0].toLowerCase().equals("3")){
					
					if(words.length == 1){
						send = "enter amount";
					}else{
						userBalance += Integer.parseInt(words[1]);
						send = "mondey added " + words[1] + "\n  new balance is : " + userBalance;
					}
					
				}
				else if(words[0].toLowerCase().equals("4")){
					send = "goodbye";
					oos.writeObject(send);
					break;
				}else{
					send = "invalid command";
				}
				
				oos.writeObject(send);
				oos.flush();
			}
			
		} catch (IOException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
}