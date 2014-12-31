package threads;

import connection.LinkHandler;
import connection.SocketHandler;

public class ConnectionHandlerThread extends Thread {

	private int port;
	SocketHandler sh = null;
	LinkHandler lh = null;
	
	public void run() {	
				
		System.out.println("Connection Handler Thread Started.");

		sh = new SocketHandler(port);
		lh = new LinkHandler();
		lh.buildLink(sh.createSocket());
		
		while(true) {
			lh.buildLink(sh.returnSocket());
		}
	}
	
	public ConnectionHandlerThread(int port) {
		this.port = port;
	}
}
