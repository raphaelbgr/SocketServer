package app.control.services.receiver.types;

import java.net.Socket;

import app.ServerMain;
import app.control.communication.SendObject;
import app.control.services.receiver.ReceiverInterface;
import app.control.sync.Broadcaster;
import app.control.sync.ClientCenter;
import net.sytes.surfael.api.model.clients.Client;
import net.sytes.surfael.api.model.messages.BroadCastMessage;
import net.sytes.surfael.api.model.messages.ServerMessage;

public class DisconnectionMessageReceiver implements ReceiverInterface {

	@Override
	public void receive(Object o, Client localClient, Socket sock) throws Exception {
		
		BroadCastMessage bcm = new BroadCastMessage();
		if (localClient != null) {
			bcm.setOwnerLogin(localClient.getLogin());
			bcm.setOwnerName(localClient.getName());
		}
		bcm.setText("Disconnected");
		bcm.setServresponse("SERVER> Disconnected");
		bcm.setDisconnect(true);
		
		if (localClient != null) {
			ClientCenter.getInstance().removeClientByClassAndSocket(localClient, sock);
			System.out.println(ServerMain.getTimestamp() + " " + localClient.getName() + " -> " + "Disconnected");
		}
		ServerMessage sm = new ServerMessage(ClientCenter.getInstance().getUsersNames());
		sm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
		Broadcaster bc = new Broadcaster();
		bc.broadCastMessage(sm);
		
		SendObject so = new SendObject();
		sm.setDisconnect(true);
		so.send(sock, sm);
		
		bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
		bc.broadCastMessage(bcm);	
		
	}

}
