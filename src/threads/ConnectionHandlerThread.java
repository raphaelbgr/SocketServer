package threads;

import connection.LinkBuilder;
import connection.ServerSocketBuilder;

public class ConnectionHandlerThread extends Thread {

	private int port;
	ServerSocketBuilder sh = null;
	LinkBuilder lh = null;
	
	public void run() {	
				
		System.out.println("Connection Handler Thread Started.");

		sh = new ServerSocketBuilder(port);
		lh = new LinkBuilder();
		lh.buildLink(sh.createSocket());
		
		while(true) {
			lh.buildLink(sh.returnSocket());
		}
	}
	
	public ConnectionHandlerThread(int port) {
		this.port = port;
	}
}
