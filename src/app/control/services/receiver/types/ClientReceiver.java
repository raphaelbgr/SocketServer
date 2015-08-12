package app.control.services.receiver.types;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.ServerMain;
import app.control.communication.SendObject;
import app.control.dao.DAO;
import app.control.services.receiver.ReceiverInterface;
import app.control.sync.Broadcaster;
import app.control.sync.ClientCenter;
import app.model.clients.Client;
import app.model.exceptions.ServerException;
import app.model.messages.BroadCastMessage;
import app.model.messages.ServerMessage;

public class ClientReceiver implements ReceiverInterface {

	@Override
	public synchronized void receive(Object o, Client localClient, Socket sock) throws IOException, ServerException, SQLException, Throwable {
		
		ClientCenter cc	= ClientCenter.getInstance();
		String cLogin = localClient.getLogin();
		Broadcaster bc = new Broadcaster();
		SendObject so = new SendObject();
		
		if (localClient.getVersion().equalsIgnoreCase(ServerMain.VERSION)) {
			if (cLogin.length() < 21) {
				DAO.connect();
				if (DAO.verifyClientPassword(localClient)) {
					if (localClient.isConnect()) {
						if (!ClientCenter.getInstance().checkNameAvaliability(cLogin)) {
							
							//Gets the client data on the database
							localClient = DAO.loadClientData(localClient);

							BroadCastMessage bcm = new BroadCastMessage();
							bcm.setOwnerLogin(cLogin);
							bcm.setOwnerName(localClient.getName());
							bcm.setConnect(true);

							cc.addClient(sock, localClient);
							bcm.setText("Connected");
							bcm.setServresponse("SERVER> Connected");
							bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
							bc.broadCastMessage(bcm);

							//Sends the list of connected people
							ServerMessage sm = new ServerMessage(ClientCenter.getInstance().getUsersNames());
							sm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
							bc.broadCastMessage(sm);
							System.out.println(getTimestamp() + localClient.toString() + " -> Connected");

							//Tells the client to enter local online mode
							ServerMessage smConnect = new ServerMessage();
							smConnect.setConnect(true);
							smConnect.setServresponse("Welcome " + localClient.getName());

							//Sends the login confirmation to client
							so.send(sock, smConnect);

						} else {
							DAO.disconnect();
							throw new ServerException(getTimestamp() + "SERVER> The login " + cLogin + " is already in use.",true, true);
						}
					}
					
					DAO.disconnect();
				} else throw new ServerException(getTimestamp() + "SERVER> Wrong Password.",true);
			} else {
				DAO.disconnect();
				throw new ServerException(getTimestamp() + " SERVER> Name greater than 20 characters.",true);
			}
		} else if (!localClient.getVersion().equalsIgnoreCase(ServerMain.VERSION)) {
			DAO.disconnect();
			throw new ServerException(getTimestamp() + "SERVER> Version " + ServerMain.VERSION + " required. Download at " + ServerMain.CLIENT_LNK,true);
		}
	}

	@Override
	public String getTimestamp() {
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateFormatted = formatter.format(new Date());
		return "["+dateFormatted+"]" + " ";
	}

}
