package app.controler.communication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SendObject {

	public synchronized void send(Socket sock, Object o) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
		oos.writeObject(o);
		oos.flush();
	}
	
}
