package app.control.services.receiver.types;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import app.ServerMain;
import app.control.communication.SendObject;
import app.control.dao.DAO;
import app.control.services.receiver.ReceiverInterface;
import app.control.sync.Broadcaster;
import app.control.sync.ClientCenter;
import net.sytes.surfael.api.model.clients.Client;
import net.sytes.surfael.api.model.exceptions.ServerException;
import net.sytes.surfael.api.model.messages.BroadCastMessage;
import net.sytes.surfael.api.model.messages.ServerMessage;

public class ClientReceiver implements ReceiverInterface {

	private boolean desktopDuplicatta = false;

	@Override
	public synchronized void receive(Object o, Client localClient, Socket sock) throws IOException, ServerException, SQLException, Throwable {

		ClientCenter cc	= ClientCenter.getInstance();
		String cLogin = localClient.getLogin();
		String cEmail = localClient.getEmail();
		Broadcaster bc = new Broadcaster();
		SendObject so = new SendObject();

		if (cLogin == null) {
			cLogin = DAO.getLoginByEmail(cEmail);
			localClient.setLogin(cLogin);
		}
		if (localClient.getVersion().equalsIgnoreCase(ServerMain.VERSION)) {
			if ((cLogin != null && cLogin.length() < 21) || cEmail != null) {
				DAO.connect();
				if (DAO.verifyClientPassword((Client)o)) {
					if (localClient.isConnect()) {
						if (localClient.getLogin() != null) {
							ClientCenter.getInstance().checkNameAvaliability(localClient.getLogin());
						}
					}
					// Checks this login is already connected
					if (!desktopDuplicatta) {
						try {
							// Verify the avaliability of it's name on the system and adds it to the online list
							cc.addClient(sock, localClient);

							// Broadcasts the new user coming in to all clients
							BroadCastMessage bcm = new BroadCastMessage();
							bcm.setOwnerLogin(cLogin);
							bcm.setOwnerName(localClient.getName());
							bcm.setOwnerEmail(localClient.getEmail());
							bcm.setConnect(true);
							bcm.setText("Connected");
							bcm.setServresponse("SERVER> Connected");
							bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
							bc.broadCastMessage(bcm);

							//Sends the list of connected people to all clients
							ServerMessage sm = new ServerMessage(ClientCenter.getInstance().getUsersNames());
							sm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
							bc.broadCastMessage(sm);
							System.out.println(ServerMain.getTimestamp() + " " + localClient.toString() + " -> Connected");

							// Authorizes the client to enter local online mode
							ServerMessage smConnect = new ServerMessage();
							smConnect.setConnect(true);
							smConnect.setServresponse("Welcome " + localClient.getName());

							//Sends the login confirmation to client
							so.send(sock, smConnect);
							so.send(sock, localClient);
						} catch (ServerException e) {
							e.printStackTrace();
						}
					} else {
						DAO.disconnect();
						throw new ServerException(ServerMain.getTimestamp() + "SERVER> The login " + cLogin + " is already in use.",true, true);
					}
					DAO.disconnect();
				} else throw new ServerException(ServerMain.getTimestamp() + "SERVER> Wrong Login or Password.",true);
			} else {
				DAO.disconnect();
				throw new ServerException(ServerMain.getTimestamp() + " SERVER> Name greater than 20 characters.",true);
			}
		} else if (!localClient.getVersion().equalsIgnoreCase(ServerMain.VERSION)) {
			DAO.disconnect();
			throw new ServerException(ServerMain.getTimestamp() + "SERVER> Version " + ServerMain.VERSION + " required. Download at " + ServerMain.CLIENT_LNK,true);
		}
	}
}