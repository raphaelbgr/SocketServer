package communication;

import sendable.Client;
import sendable.ConnectionMessage;
import sendable.DisconnectionMessage;
import sendable.Message;
import sendable.NormalMessage;
import sync.ClientCenter;
import exceptions.ServerException;

public class ObjectHandler {

	ClientCenter cc 		= ClientCenter.getInstance();
	ReceiveObject receiver 	= new ReceiveObject();
	SendObject sender 		= new SendObject();

	public void handleObject(Object o) throws ServerException {
		if (o instanceof Message) {
			if (o instanceof NormalMessage) {
				handleNormalMessage((NormalMessage) o);
			} else if (o instanceof ConnectionMessage) {
				handleConnectionMessage((ConnectionMessage) o);
			} else if (o instanceof DisconnectionMessage) {
				handleDisconnectionMessage((DisconnectionMessage) o);
			} else if (o instanceof Client) {
				handleClient((Client) o);
			}
		} else if (o instanceof ServerException) {
			System.err.println(((ServerException)o).getMessage());
		}
	}

	private void handleNormalMessage(NormalMessage m) {
		System.out.println(m.toString());
	}

	private void handleDisconnectionMessage(DisconnectionMessage m) {
		System.err.println(m.toString());
	}

	private void handleConnectionMessage(ConnectionMessage m) {
		System.out.println(m.toString());
	}
	
	private void handleClient(Client c) throws ServerException {
		try {
			cc.addUser(c);
		} catch (ServerException e) {
			e.toString();
		}
	}

}
