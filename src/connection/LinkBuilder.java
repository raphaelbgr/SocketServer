package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import threads.ClientReceiverThread;
import threads.Sender;


public class LinkBuilder {

	private Socket sock = null;

	public void buildLink(ServerSocket jsock) {

		try {
			this.sock = jsock.accept();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		//			RECEIVER SERVERSIDE THREAD
		ClientReceiverThread rc = new ClientReceiverThread(sock);
		Thread t2 = new Thread(rc);
		t2.start();
		
		//			SENDER SERVERSIDE THREAD		
		Sender sc = new Sender(sock);
		sc.start();

	}

}
