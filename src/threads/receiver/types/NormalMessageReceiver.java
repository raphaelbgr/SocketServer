package threads.receiver.types;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import sendable.Message;
import sendable.NormalMessage;
import sync.Broadcaster;
import sync.ClientCenter;
import threads.receiver.Receiver;
import dao.DAO;
import exceptions.ServerException;

public class NormalMessageReceiver implements Receiver {

	@Override
	public void receive(Object o) throws IOException, ServerException {
		Message m = (Message) o;
		if (m.getText().length() < 101) {
			//BUILD DE MESSAGE RESPONSE
			m.setServresponse("SERVER> Received");
			m.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
			m.setOwnerName(m.getOwnerName());
			m.setOwnerLogin(m.getOwnerLogin());
			m.setServerReceivedtime();
			
			//BUILDS THE BROADCASTER AND SENDS THE MESSAGE
			Broadcaster bc = new Broadcaster();
			bc.broadCastMessage(m);
			
			//IF SUCCESSFULL, THIS WILL BE PRINTED OUT ON CONSOLE
			System.out.println(getTimestamp() + m.getOwnerName() + " -> " + ((Message)o).getText());
			
			//ATTEMPS TO STORE THE MESSAGE ON THE DB
			try {
				DAO.connect();
				DAO.storeMessage((NormalMessage)o);
				DAO.updateSentMsgs(((Message)o));
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
	
	private String getTimestamp() {
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateFormatted = formatter.format(new Date());
		return "["+dateFormatted+"]" + " ";
	}
}
