package sync;

import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import sendable.Client;
import servermain.ServerMain;
import exceptions.ServerException;



public class ClientCenter {

	private static ClientCenter cc 				= null;
	private HashMap<String,Client> userClasses 	= new HashMap<String,Client>();
	private HashMap<Socket,Client> clientSockets= new HashMap<Socket,Client>();
	
	private HashSet<Client> usersNames 			= new HashSet<Client>();
	private HashSet<Socket> sockets				= new HashSet<Socket>();
	private Vector<String> onlineUserList		= new Vector<String>();
	

	public HashSet<Socket> getSockets() {
		return sockets;
	}

	public synchronized void addUser(Client c) throws ServerException {
		if (!usersNames.add(c)) {
			throw new ServerException("Client name already in use: " + c.getName());
		}
		onlineUserList.add(c.toString());
	}

	public synchronized void addClient(Socket sock, Client c) throws Throwable {
		if (!userClasses.containsKey(c.getName())) {
			userClasses.put(c.getName(), c);
			sockets.add(sock);
			usersNames.add(c);
			onlineUserList.add(c.toString());
			clientSockets.put(sock, c);
		} else {
			ServerException se = new ServerException(ServerMain.getTimestamp() + " SERVER> The name " + c.getName() + " is already in use.", true);
			se.setToDisconnect(true);
			throw se;
		}
	}

/*	public synchronized void removeClientByClass(Client c) throws Throwable {
		if(userClasses.containsKey(c.getName())) {
			Client c1 = userClasses.get(c.getName());
			sockets.remove(c1.getSock());
			userClasses.remove(c.getName());
			usersNames.remove(c);
			onlineUserList.remove(c.toString());
			;		} else {
				throw new ServerException("Client is not on the list.");
			}
	}*/

	public synchronized void removeClientByName(String s) throws Throwable {
		if(userClasses.containsKey(s)) {
			Client c = userClasses.get(s);
			clientSockets.remove(c);
			sockets.remove(c.getSock());
			usersNames.remove(c);
			userClasses.remove(s);
			onlineUserList.remove(s);
		} else {
			throw new ServerException("Client is not on the list.");
		}
	}

	public HashMap<String, Client> getChash() {
		return userClasses;
	}

	public void setChash(HashMap<String, Client> chash) {
		this.userClasses = chash;
	}

	//SINGLETON BLOCK
	private ClientCenter(){	}
	public static ClientCenter getInstance() {
		if (cc == null) {
			cc = new ClientCenter();
		}
		return cc;
	}

	public HashSet<Client> getUsersNames() {
		return usersNames;
	}

	public Vector<String> getOnlineUserList() {
		return onlineUserList;
	}

	public void setOnlineUserList(Vector<String> onlineUserList) {
		this.onlineUserList = onlineUserList;
	}

	public HashMap<Socket, Client> getClientSockets() {
		return clientSockets;
	}
}
