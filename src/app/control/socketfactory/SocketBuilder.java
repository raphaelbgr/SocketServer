package app.control.socketfactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import app.control.services.ReceiverManager;
import app.control.sync.ClientCenter;


public class SocketBuilder {

	private Socket sock = null;

	public synchronized void buildLink(ServerSocket jsock) throws IOException {
		try {
			// CREATES THE SOCKET WITH THE CLIENT AND START A NEW THREAD ISOLATING HIM/HER
			this.sock = jsock.accept();
			ClientCenter.getInstance().getSockets().add(this.sock);
			ReceiverManager rc = new ReceiverManager(sock);
			Thread t2 = new Thread(rc);
			t2.start();
		} catch (Exception e) {
			// DUMPS THE SOCKET AND RESTARTS THE SERVICE
			e.printStackTrace();
			if (sock != null) {
				ClientCenter.getInstance().removeClientBySocket(sock);
			}
			ServerSocketBuilder.dumpServerSocket();
			ServerSocketBuilder.createSocket();
		}
	}
}