package communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import exceptions.ServerException;
import sendable.Message;

public class ReceiveObject {

	@SuppressWarnings("unused")
	public Object receive(Socket sock) throws ClassNotFoundException, IOException, ServerException {
		ObjectHandler oh = new ObjectHandler();
		ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
		if (ois != null) {
			Object o = ois.readObject();
			isMessageConfigureClass(o, sock);
			oh.handleObject(o);
			return o;
		}
		return null;
	}

	private Object isMessageConfigureClass(Object o, Socket sock) {
		if (o instanceof Message) {
			Message m = (Message) o;
			((Message)o).setIp(sock.getInetAddress().getHostAddress());
			m = detectNetwork(sock, m);
			m.setIp(sock.getInetAddress().getHostAddress());
			return m;
		}
		return o;
	}

	private Message detectNetwork(Socket sock, Message m) {
		if (sock.getInetAddress().isLoopbackAddress()) {
			m.setNetwork(0);
		} else if (sock.getInetAddress().isLinkLocalAddress()) {
			m.setNetwork(1);
		} else {
			m.setNetwork(2);
		}
		return m;
	}

}
