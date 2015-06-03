package threads.receiver;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import sendable.clients.Client;
import exceptions.ServerException;

public interface ReceiverInterface {

	public void receive(Object o, Client localClient, Socket sock) throws IOException, ServerException, SQLException, Throwable;
	String getTimestamp();
}
