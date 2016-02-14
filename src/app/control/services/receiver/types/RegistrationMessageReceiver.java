package app.control.services.receiver.types;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import app.control.communication.SendObject;
import app.control.dao.DAO;
import app.control.services.receiver.ReceiverInterface;
import net.sytes.surfael.api.model.clients.Client;
import net.sytes.surfael.api.model.clients.NewClient;
import net.sytes.surfael.api.model.exceptions.ServerException;
import net.sytes.surfael.api.model.messages.ServerMessage;

public class RegistrationMessageReceiver implements ReceiverInterface {

	@Override
	public void receive(Object o, Client localClient, Socket sock) throws IOException, ServerException, SQLException, Throwable {

		DAO.registerUser((Client) o);
		ServerMessage regResponse = new ServerMessage();
		regResponse.setRegisterSuccess(true);
		regResponse.setServresponse("Registered " + ((Client) o).getName() + " sucessfully.");

		// Sends the login confirmation to client
		SendObject so = new SendObject();
		so.send(sock, regResponse);

		if (((Client) o).getFbToken() == null) {
			sock.close();
		}
	}
}
