package app.controler.communication;

import app.control.sync.ClientCenter;
import app.model.clients.Client;
import app.model.exceptions.ServerException;
import app.model.messages.DisconnectionMessage;
import app.model.messages.Message;
import app.model.messages.NormalMessage;

public class ObjectHandler {

	ClientCenter cc 		= ClientCenter.getInstance();
	ReceiveObject receiver 	= new ReceiveObject();
	SendObject sender 		= new SendObject();

	public void handleObject(Object o) throws ServerException {
		if (o instanceof Message) {
			if (o instanceof NormalMessage) {
				handleNormalMessage((NormalMessage) o);
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
//		System.out.println(m.toString());
	}

	private void handleDisconnectionMessage(DisconnectionMessage m) {
//		System.err.println(m.toString());
	}

	private synchronized void handleClient(Client c) throws ServerException {
		cc.addUser(c);
		System.out.println(c.toString() + " connected");
	}

}
