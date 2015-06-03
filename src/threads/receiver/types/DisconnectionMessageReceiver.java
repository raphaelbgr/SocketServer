package threads.receiver.types;

import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import sendable.clients.Client;
import sendable.messages.BroadCastMessage;
import sendable.messages.ServerMessage;
import sync.Broadcaster;
import sync.ClientCenter;
import threads.receiver.ReceiverInterface;

public class DisconnectionMessageReceiver implements ReceiverInterface {

	@Override
	public void receive(Object o, Client localClient, Socket sock) throws Throwable {
		
		BroadCastMessage bcm = new BroadCastMessage();
		bcm.setOwnerLogin(localClient.getLogin());
		bcm.setOwnerName(localClient.getName());
		bcm.setText("Disconnected");
		bcm.setServresponse("SERVER> Disconnected");
		ClientCenter.getInstance().removeClientByClass(localClient);
		
		ServerMessage sm = new ServerMessage(ClientCenter.getInstance().getUsersNames());
		Broadcaster bc = new Broadcaster();
		bc.broadCastMessage(sm);
		
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
