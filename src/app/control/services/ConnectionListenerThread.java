package app.control.services;

import app.ServerMain;
import app.control.socketfactory.ServerSocketBuilder;
import app.control.socketfactory.SocketBuilder;

public class ConnectionListenerThread extends Thread {

	ServerSocketBuilder ssbuilder = null;
	SocketBuilder sbuilder = null;
	
	public void run() {	
		ssbuilder = new ServerSocketBuilder();
		sbuilder = new SocketBuilder();
		boolean suicide = false;
		
		do {
			try {
				// DUAL CALL -- SERVERSOCKETBUILDER LISTENS INCOMING CONNECTION AND WHEN IT RECEIVES, PASS IT TOO THE SOCKETBUILDER
				sbuilder.buildLink(ServerSocketBuilder.createSocket());
				ServerMain.RECEIVE_CONN = true;
				sbuilder.buildLink(ServerSocketBuilder.returnSocket());
			} catch (Exception e) {
				// DUMP THE SOCKETLISTENER, KILLS ITSELFT AND RESTARTS THE SERVICE
				ServerSocketBuilder.dumpServerSocket();
				ConnectionListenerThread ch = new ConnectionListenerThread();
				Thread t1 = new Thread(ch);
				t1.start();
				e.printStackTrace();
				break;
			} finally {
				suicide = true;
			}
		} while (!suicide);
		
	}
}
