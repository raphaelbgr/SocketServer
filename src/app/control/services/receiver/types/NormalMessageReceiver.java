package app.control.services.receiver.types;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.control.dao.DAO;
import app.control.services.receiver.ReceiverInterface;
import app.control.sync.Broadcaster;
import app.control.sync.ClientCenter;
import app.model.clients.Client;
import app.model.exceptions.ServerException;
import app.model.messages.NormalMessage;

public class NormalMessageReceiver implements ReceiverInterface {
	
	@Override
	public String getTimestamp() {
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateFormatted = formatter.format(new Date());
		return "["+dateFormatted+"]" + " ";
	}

	@Override
	public void receive(Object o, Client localClient, Socket sock) throws IOException, ServerException, SQLException {
		NormalMessage nm = (NormalMessage) o;
		if (nm.getText().length() < 101) {
			
			//BUILD DE MESSAGE RESPONSE
			nm.setServresponse("SERVER> Received");
			nm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
			nm.setOwnerName(localClient.getName());
			nm.setOwnerLogin(localClient.getLogin());
			nm.setServerReceivedtime();
			
			//BUILDS THE BROADCASTER AND SENDS THE MESSAGE TO EVERYONE
			Broadcaster bc = new Broadcaster();
			bc.broadCastMessage(nm);
			
			//IF SUCCESSFULL, THIS WILL BE PRINTED OUT ON CONSOLE
			System.out.println(getTimestamp() + nm.getOwnerName() + " -> " + nm.getText());
			
			//ATTEMPS TO STORE THE MESSAGE ON THE DB
			try {
				DAO.connect();
				DAO.storeMessage(nm);
				DAO.updateSentMsgs(nm);
			} catch (SQLException e) {
				System.err.println(getTimestamp() + "SERVER> Could not store this message on the database.");
				e.printStackTrace();
			} finally {
				try {
					DAO.disconnect();
				} catch (SQLException e) {
					
				}
			}
		} else {
			throw new ServerException(getTimestamp() + " SERVER> Message greater than 100 characters.");
		}
	}
}
