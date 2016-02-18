package app.control.services.receiver.types;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import app.control.communication.SendObject;
import app.control.dao.DAO;
import app.control.services.receiver.ReceiverInterface;
import net.sytes.surfael.api.model.clients.Client;
import net.sytes.surfael.api.model.exceptions.ServerException;
import net.sytes.surfael.api.model.messages.History;
import net.sytes.surfael.api.model.messages.ServerMessage;

public class ServerMessageProcessor implements ReceiverInterface {

	@Override
	public void receive(Object o, Client localClient, Socket sock)
			throws IOException, ServerException, SQLException, Throwable {
		ServerMessage sm = (ServerMessage)o;
		
		History data = null;
		
		if (sm.getRequest() != null) {
			switch (sm.getRequest()) {
			case "history": {
				data = DAO.getHistory(sm.getRowLimit());
				SendObject so = new SendObject();
				so.send(sock, data);
				break;
			}
			case "androidhistory": {
				data = DAO.getAndroidHistory(sm.getRowLimit());
				SendObject so = new SendObject();
				so.send(sock, data);
				break;
			}
			default:
				break;
			}
		}
	}

}
