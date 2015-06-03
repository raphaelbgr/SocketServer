package threads.receiver.types;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import sendable.clients.Client;
import sendable.messages.NormalMessage;
import sync.Broadcaster;
import sync.ClientCenter;
import threads.receiver.ReceiverInterface;
import dao.DAO;
import exceptions.ServerException;

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
					sock.close();
					sock = null;
					DAO.disconnect();
				} catch (SQLException e) {
					
				}
			}
		} else {
			throw new ServerException(getTimestamp() + " SERVER> Message greater than 100 characters.");
		}
	}
}
