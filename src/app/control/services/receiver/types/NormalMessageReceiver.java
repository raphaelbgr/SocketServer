package app.control.services.receiver.types;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import app.ServerMain;
import app.control.dao.DAO;
import app.control.services.receiver.ReceiverInterface;
import app.control.sync.Broadcaster;
import app.control.sync.ClientCenter;
import net.sytes.surfael.api.model.clients.Client;
import net.sytes.surfael.api.model.exceptions.ServerException;
import net.sytes.surfael.api.model.messages.NormalMessage;

public class NormalMessageReceiver implements ReceiverInterface {

	@Override
	public void receive(Object o, Client localClient, Socket sock) throws IOException, ServerException, SQLException {
		NormalMessage nm = (NormalMessage) o;
		if (nm.getText().length() < 1001) {
			
			//BUILD DE MESSAGE RESPONSE
			nm.setServresponse("SERVER> Received");
			nm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
			nm.setOwnerName(localClient.getName());
			nm.setOwnerLogin(localClient.getLogin());
			nm.setServerReceivedtime();
			nm.setSenderId(localClient.getId());

			if (localClient.getPhotoUrl() != null) {
				nm.setSenderPhotoUrl(localClient.getPhotoUrl());
			}
			
			//BUILDS THE BROADCASTER AND SENDS THE MESSAGE TO EVERYONE
			Broadcaster bc = new Broadcaster();
			bc.broadCastMessage(nm);
			
			//IF SUCCESSFULL, THIS WILL BE PRINTED OUT ON CONSOLE
			System.out.println(ServerMain.getTimestamp() + " " + nm.getOwnerName() + " -> " + nm.getText());
			
			//ATTEMPS TO STORE THE MESSAGE ON THE DB
			DAO.aSyncStoreMessage(nm);
		} else {
			throw new ServerException(ServerMain.getTimestamp() + " SERVER> Message greater than 1000 characters.");
		}
	}
}
