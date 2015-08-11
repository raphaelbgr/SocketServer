package app.control.sync;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import app.model.messages.Message;


public class Broadcaster {

	ClientCenter cc = ClientCenter.getInstance();
	
	public synchronized void broadCastMessage(Message bm) throws IOException {
		for (Socket sock : ClientCenter.getInstance().getSockets()) {
			if (sock != null) {
				if (!ClientCenter.getInstance().getSockets().isEmpty()) {
					if (sock != null) {
						if (!sock.isClosed() && sock.getOutputStream() != null) {
							ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
							bm.setTimestamp();
							oos.writeObject(bm);
							oos.flush();
						}
					}
				}	
			}
		}
	}
}
