package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import threads.ReceiveFromClientThread;
import threads.SendToClientThread;


public class LinkHandler {

	private Socket link = null;

	public void buildLink(ServerSocket jsock) {

		try {
			this.link = jsock.accept();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		//			RECEIVER SERVERSIDE THREAD
		ReceiveFromClientThread rc = new ReceiveFromClientThread(link);
		Thread t2 = new Thread(rc);
		t2.start();
		
		//			SENDER SERVERSIDE THREAD		
		SendToClientThread sc = new SendToClientThread(link);
		sc.start();

	}

	public LinkHandler() {
	}
}
