package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import sync.ClientCenter;
import threads.ReceiverManager;


public class LinkBuilder {

	private Socket sock = null;

	public synchronized void buildLink(ServerSocket jsock) {

		try {
			this.sock = jsock.accept();
			ClientCenter.getInstance().getSockets().add(this.sock);
		} catch (SocketException e) {
			e.printStackTrace();
			System.out.println("Socket Ex! On class LinkBuilder Line 22");
		} catch (IOException e) {
			e.printStackTrace();
		}

//		RECEIVER SERVERSIDE THREAD
		if (sock != null) {
			ReceiverManager rc = new ReceiverManager(sock);
			Thread t2 = new Thread(rc);
			t2.start();
		} else {
			try {
				ServerSocketBuilder.jsock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				ServerSocketBuilder.jsock = null;
				try {
					ServerSocketBuilder.dumpSocket();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}

}
