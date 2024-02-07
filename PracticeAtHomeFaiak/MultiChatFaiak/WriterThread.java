import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class WriterThread implements Runnable{
	ObjectOutputStream oos;
	
	public WriterThread( ObjectOutputStream OOS ) {
		this.oos = OOS;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		System.out.println("write something : ");
		Scanner sc = new Scanner(System.in);
		try {
			while ( true ) {
				String in = sc.nextLine();
				
				if ( in instanceof String && in.equals("exit") ) {
					System.out.println("exiting");
					break;
				}
				
				try {
					Integer n = Integer.parseInt(in);
					oos.writeObject(n);
				} catch (NumberFormatException e) {
					oos.writeObject(in);
				}
				
				
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
