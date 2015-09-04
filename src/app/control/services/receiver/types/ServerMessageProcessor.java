package app.control.services.receiver.types;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import app.control.communication.SendObject;
import app.control.dao.DAO;
import app.control.services.receiver.ReceiverInterface;
import app.model.clients.Client;
import app.model.exceptions.ServerException;
import app.model.messages.History;
import app.model.messages.ServerMessage;

public class ServerMessageProcessor implements ReceiverInterface {

	@Override
	public void receive(Object o, Client localClient, Socket sock)
			throws IOException, ServerException, SQLException, Throwable {
		ServerMessage sm = (ServerMessage)o;
		
		History data = null;
		
		switch (sm.getRequest()) {
		case "history":
			data = DAO.getHistory(sm.getRowLimit());
			SendObject so = new SendObject();
			so.send(sock, data);
			break;
		default:
			break;
		}
		
		
	}

}
