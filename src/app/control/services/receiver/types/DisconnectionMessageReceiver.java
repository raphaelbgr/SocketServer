package app.control.services.receiver.types;

import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.control.services.receiver.ReceiverInterface;
import app.control.sync.Broadcaster;
import app.control.sync.ClientCenter;
import app.controler.communication.SendObject;
import app.model.clients.Client;
import app.model.messages.BroadCastMessage;
import app.model.messages.ServerMessage;

public class DisconnectionMessageReceiver implements ReceiverInterface {

	@Override
	public void receive(Object o, Client localClient, Socket sock) throws Throwable {
		
		BroadCastMessage bcm = new BroadCastMessage();
		bcm.setOwnerLogin(localClient.getLogin());
		bcm.setOwnerName(localClient.getName());
		bcm.setText("Disconnected");
		bcm.setServresponse("SERVER> Disconnected");
		bcm.setDisconnect(true);
		
		ClientCenter.getInstance().removeClientByClassAndSocket(localClient, sock);
		ServerMessage sm = new ServerMessage(ClientCenter.getInstance().getUsersNames());
		sm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
		Broadcaster bc = new Broadcaster();
		bc.broadCastMessage(sm);
		
		SendObject so = new SendObject();
		sm.setDisconnect(true);
		so.send(sock, sm);
		
		System.out.println(this.getTimestamp()+ localClient.getName() + " -> " + "Disconnected");	
		
		bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
		bc.broadCastMessage(bcm);	
		
	}

	@Override
	public String getTimestamp() {
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateFormatted = formatter.format(new Date());
		return "["+dateFormatted+"]" + " ";
	}
	
}
