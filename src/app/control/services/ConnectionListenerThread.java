package app.control.services;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import app.ServerMain;

public class ConnectionListenerThread extends Thread {


	private ServerSocket jsock;

	public void run() {	

		do {
			try {
				// SERVERSOCKETBUILDER LISTENS INCOMING CONNECTION AND WHEN IT RECEIVES, PASS IT TOO THE SOCKETBUILDER
				if (jsock != null) {
					jsock.close();
				}
				jsock = new ServerSocket(ServerMain.PORT);
				
				// ACCEPTS THE INCOMING CONNECTION AND CREATES A SOCKET
				Socket sock = jsock.accept();
				
				// ISOLATES THIS SOCKET TO A SEPARATE THREAD
				ReceiverManager rc = new ReceiverManager(sock);
				Thread t2 = new Thread(rc);
				t2.start();
			} catch (BindException e) {
				e.printStackTrace();
				takeABreak(5000);
			} catch (IOException e) {
				// DUMP THE SOCKETLISTENER
				e.printStackTrace();
				takeABreak(5000);
			}
		} while (ServerMain.RECEIVE_CONN);
	}
	
	private void takeABreak(int duration) {
		try {
			System.err.println("Taking a break from port " + ServerMain.PORT + " of " + duration + " secs...");
			Thread.sleep(duration);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
}
