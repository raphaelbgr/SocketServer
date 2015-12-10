package app.control.services.receiver;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import net.sytes.surfael.api.model.clients.Client;
import net.sytes.surfael.api.model.exceptions.ServerException;

public interface ReceiverInterface {

	public void receive(Object o, Client localClient, Socket sock) throws IOException, ServerException, SQLException, Throwable;
}
