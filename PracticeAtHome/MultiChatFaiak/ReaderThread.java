import java.io.IOException;
import java.io.ObjectInputStream;

public class ReaderThread implements Runnable{
	
	ObjectInputStream ois;
	
	ReaderThread(ObjectInputStream ois){
		this.ois = ois;
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		
		try {
			while(true) {
				System.out.println(ois.readObject());
			}
		}catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	
}
