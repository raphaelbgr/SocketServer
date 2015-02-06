package sync;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import sendable.Message;


public class Broadcaster {

	ClientCenter cc = ClientCenter.getInstance();
	
	public void broadCastMessage(Message bm) throws IOException {
		
		for (Socket sock : ClientCenter.getInstance().getSockets()) {
			ObjectOutputStream oos = new ObjectOutputStream(sock.getOutputStream());
			bm.setServresponse("SERVER> Broadcast message.");
			bm.setTimestamp();
//			bm.setOwner(bm.getOwner());
			oos.writeObject(bm);
			oos.flush();
		}
		
//		for (Client c : cc.getChash().values()) {
//		    try {
//				ObjectOutputStream oos = new ObjectOutputStream(c.getSock().getOutputStream());
//				bm.setType("broadcast");
//				bm.setServresponse("Broadcast message.");
//				oos.writeObject(bm);
//				oos.flush();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
	}
}
