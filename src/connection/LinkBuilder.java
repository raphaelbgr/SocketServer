package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import sync.ClientCenter;
import threads.ReceiverFacade;


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
		ReceiverFacade rc = new ReceiverFacade(sock);
		Thread t2 = new Thread(rc);
		t2.start();
		
	}

}
