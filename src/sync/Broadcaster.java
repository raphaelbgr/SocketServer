package sync;

import java.io.IOException;
import java.io.ObjectOutputStream;

import sendable.Message;
import clientserverside.Client;


public class Broadcaster {

	ClientCenter cc = ClientCenter.getInstance();
	
	public void broadCastMessage(Message m) {
		
		for (Client c : cc.getChash().values()) {
		    try {
				ObjectOutputStream oos = new ObjectOutputStream(c.getSock().getOutputStream());
				m.setType("broadcast");
				m.setServresponse("Broadcast message.");
				oos.writeObject(m);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
