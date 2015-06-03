package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import sync.ClientCenter;
import threads.ReceiverManager;


public class LinkBuilder {

	private Socket sock = null;

	public void buildLink(ServerSocket jsock) {

		try {
			this.sock = jsock.accept();
			ClientCenter.getInstance().getSockets().add(this.sock);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//			RECEIVER SERVERSIDE THREAD
		ReceiverManager rc = new ReceiverManager(sock);
		Thread t2 = new Thread(rc);
		t2.start();
		
	}

}
