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
	private HashMap<Integer,Client> clientPorts = new HashMap<Integer,Client>();

	private HashSet<Client> usersNames 			= new HashSet<Client>();
	private HashSet<Socket> sockets				= new HashSet<Socket>();
	private Vector<String> onlineUserList		= new Vector<String>();
	private Client c							= null;


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
			clientPorts.put(c.getLocalPort(), c);
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
		c = null;
		if(userClasses.containsKey(s)) {
			c = userClasses.get(s);
			clientSockets.remove(c);
			sockets.remove(c.getSock());
			usersNames.remove(c);
			userClasses.remove(s);
			onlineUserList.remove(s);
			clientPorts.remove(c.getLocalPort());
		} else {
			throw new ServerException(c.getName() + " is already offline.");
		}
	}

	@SuppressWarnings("null")
	public synchronized Client getClientByPort(Integer i) {
		Client c = null;
		if(clientPorts.containsKey(i)) {
			return clientPorts.get(i);
		} else {
			try {
				if (c.getName() != null) {
					throw new ServerException(c.getName() + " is already offline.");
				}
				throw new ServerException("Client is already offline.");
			} catch (ServerException e) {
			}
		}
		return c;
	}
	
	public synchronized void removeClientByPort(Integer i) throws Throwable {
		c = null;
		if(clientPorts.containsKey(i)) {
			c = clientPorts.get(i);
			clientSockets.remove(c);
			sockets.remove(c.getSock());
			usersNames.remove(c);
			userClasses.remove(c.getName());
			onlineUserList.remove(c.getName());
			clientPorts.remove(i);
		} else {
			throw new ServerException(c.getName() + " is already offline.");
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
