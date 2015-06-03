package sync;

import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import sendable.clients.Client;
import servermain.ServerMain;
import exceptions.ServerException;



public class ClientCenter {

	private static ClientCenter cc 					= null;
	private HashMap<String,Client> loginsToClients 	= new HashMap<String,Client>();
	private HashMap<Socket,Client> socketToClient	= new HashMap<Socket,Client>();
	private HashMap<String,Socket> namestToSocket	= new HashMap<String,Socket>();
	private HashMap<Integer,Client> portToClients 	= new HashMap<Integer,Client>();

	private HashSet<Client> usersNames 				= new HashSet<Client>();
	private HashSet<Socket> sockets					= new HashSet<Socket>();
	private Vector<String> onlineUserList			= new Vector<String>();
	private Client c								= null;
//	private Set<String> usedNames					= new HashSet<String>();


	public HashSet<Socket> getSockets() {
		return sockets;
	}

	public synchronized boolean checkNameAvaliability(String s) throws ServerException {
		return onlineUserList.contains(s);
	}
	
	public synchronized void addUser(Client c) throws ServerException {
		if (!usersNames.add(c)) {
			throw new ServerException("Client login already in use: " + c.getLogin());
		}
		onlineUserList.add(c.toString());
	}

	public synchronized void addClient(Socket sock, Client c) throws Throwable {
		if (!loginsToClients.containsKey(c.getLogin())) {
			loginsToClients.put(c.getLogin(), c);
			sockets.add(sock);
			usersNames.add(c);
			onlineUserList.add(c.toString());
			socketToClient.put(sock, c);
			portToClients.put(c.getLocalPort(), c);
			namestToSocket.put(c.getLogin(), sock);
		} else {
			ServerException se = new ServerException(ServerMain.getTimestamp() + " SERVER> The name " + c.getLogin() + " is already in use.", true);
			se.setToDisconnect(true);
			throw se;
		}
	}

	public synchronized void removeClientByClass(Client c) throws Throwable {
		socketToClient.remove(c);
		sockets.remove(namestToSocket.get(c.getLogin()));
		usersNames.remove(c);
		loginsToClients.remove(c.getLogin());
		onlineUserList.remove(c.toString());
		portToClients.remove(c.getLocalPort());
	}

	public synchronized void removeClientByLogin(String s) throws Throwable {
		c = null;
		if(loginsToClients.containsKey(s)) {
			c = loginsToClients.get(s);
			socketToClient.remove(c);
			sockets.remove(namestToSocket.get(s));
			usersNames.remove(c);
			loginsToClients.remove(s);
			onlineUserList.remove(s);
			portToClients.remove(c.getLocalPort());	
		} else {
			throw new ServerException(c.getLogin() + " is already offline.");
		}
	}

	public synchronized Client getClientByPort(Integer i) {
		Client c = null;
		if(portToClients.containsKey(i)) {
			return portToClients.get(i);
		} else {
			try {
//				if (c != null || c.getLogin() != null) {
//					throw new ServerException(c.getLogin() + " not found on connected list.");
//				}
				throw new ServerException("Client is already offline.");
			} catch (ServerException e) {
			}
		}
		return c;
	}
	
	public synchronized void removeClientByPort(Integer i) throws Throwable {
		c = null;
		if(portToClients.containsKey(i)) {
			c = portToClients.get(i);
			socketToClient.remove(c);
			sockets.remove(namestToSocket.get(c.getLogin()));
			namestToSocket.remove(c.getLogin());
			usersNames.remove(c);
			loginsToClients.remove(c.getLogin());
			onlineUserList.remove(c.getLogin());
			portToClients.remove(i);
		} else {
			throw new ServerException(c.getLogin() + " is already offline.");
		}
	}

	public HashMap<String, Client> getChash() {
		return loginsToClients;
	}

	public void setChash(HashMap<String, Client> chash) {
		this.loginsToClients = chash;
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
		return socketToClient;
	}
}
