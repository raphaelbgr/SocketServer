package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import threads.ReceiveFromClientThread;


public class LinkBuilder {

	private Socket sock = null;

	public void buildLink(ServerSocket jsock) {

		try {
			this.sock = jsock.accept();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		//			RECEIVER SERVERSIDE THREAD
		ReceiveFromClientThread rc = new ReceiveFromClientThread(sock);
		Thread t2 = new Thread(rc);
		t2.start();
		
	}

}
