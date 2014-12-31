package sync;

import java.net.Socket;
import java.util.HashMap;

import sendable.Message;
import clientserverside.Client;
import exceptions.ServerException;



public class ClientCenter {
	
	private static ClientCenter cc = null;
	private HashMap<String,Client> chash = new HashMap<String,Client>();
	
	private ClientCenter(){		
	}
	
	public static ClientCenter getInstance() {
		if (cc == null) {
			cc = new ClientCenter();
		}
		return cc;
	}
	
	public synchronized void addClient(Socket sock, Message m) throws Throwable {
		if (!chash.containsKey(m.getOwner())) {
			chash.put(m.getOwner(),new Client(sock, m));
		} else {
			throw new ServerException("Client name already in use.");
		}
	}
	
	public synchronized void revoveClientByClass(Client c) throws Throwable {
		if(chash.containsKey(c.getName())) {
			chash.remove(c.getName());
		} else {
			throw new ServerException("Client is not on the list.");
		}
	}
	
	public synchronized void removeClientByName(String s) throws Throwable {
		if(chash.containsKey(s)) {
			chash.remove(s);
		} else {
			throw new ServerException("Client is not on the list.");
		}
	}

	public HashMap<String, Client> getChash() {
		return chash;
	}

	public void setChash(HashMap<String, Client> chash) {
		this.chash = chash;
	}
	
/*	public synchronized void killIddleClients() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			hash
		}
	}*/
}
