package sync;

import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

import sendable.Client;
import sendable.Message;
import servermain.ServerMain;
import exceptions.ServerException;



public class ClientCenter {

	private static ClientCenter cc 				= null;
	private HashMap<String,Client> userNames 	= new HashMap<String,Client>();
	private HashSet<Client> users 				= new HashSet<Client>();


	public synchronized void addUser(Client c) throws ServerException {
		if (!users.add(c)) {
			throw new ServerException("Client name already in use: " + c.getName());
		}
	}

	public synchronized void addClient(Socket sock, Message m) throws Throwable {
		if (!userNames.containsKey(m.getOwner())) {
			userNames.put(m.getOwner(),new Client(sock));
		} else {
			throw new ServerException("Client name already in use.");
		}
	}
	
	public synchronized void addClient(Socket sock, Client c) throws Throwable {
		if (!userNames.containsKey(c.getName())) {
			userNames.put(c.getName(), c);
		} else {
			throw new ServerException(ServerMain.getTimestamp() + " SERVER> The name " + c.getName() + " is already in use.", true);
		}
	}

	public synchronized void removeClientByClass(Client c) throws Throwable {
		if(userNames.containsKey(c.getName())) {
			userNames.remove(c.getName());
		} else {
			throw new ServerException("Client is not on the list.");
		}
	}

	public synchronized void removeClientByName(String s) throws Throwable {
		if(userNames.containsKey(s)) {
			userNames.remove(s);
		} else {
			throw new ServerException("Client is not on the list.");
		}
	}

	public HashMap<String, Client> getChash() {
		return userNames;
	}

	public void setChash(HashMap<String, Client> chash) {
		this.userNames = chash;
	}

	//SINGLETON BLOCK
	private ClientCenter(){	}
	public static ClientCenter getInstance() {
		if (cc == null) {
			cc = new ClientCenter();
		}
		return cc;
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
