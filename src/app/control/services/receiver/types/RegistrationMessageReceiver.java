package app.control.services.receiver.types;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import app.control.communication.SendObject;
import app.control.dao.DAO;
import app.control.services.receiver.ReceiverInterface;
import app.model.clients.Client;
import app.model.clients.NewClient;
import app.model.exceptions.ServerException;
import app.model.messages.ServerMessage;

public class RegistrationMessageReceiver implements ReceiverInterface {

	@Override
	public void receive(Object o, Client localClient, Socket sock) throws IOException, ServerException, SQLException, Throwable {
		NewClient nc = (NewClient) o;
		if (DAO.registerUser(nc)) {
			ServerMessage regResponse = new ServerMessage();
			regResponse.setRegisterSuccess(true);
			regResponse.setServresponse("Registered " + nc.getName() + " sucessfully.");
			
			//Sends the login confirmation to client
			SendObject so = new SendObject();
			so.send(sock, regResponse);
		}
	}

}
