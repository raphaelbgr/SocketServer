package app.control.sync;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import app.ServerMain;
import app.control.communication.SendObject;
import app.control.dao.DAO;
import net.sytes.surfael.api.model.clients.Client;
import net.sytes.surfael.api.model.exceptions.ServerException;
import net.sytes.surfael.api.model.messages.BroadCastMessage;



public class ClientCenter {

	private static ClientCenter cc 					= null;
	private HashMap<String,Client> loginsToClients 	= new HashMap<String,Client>();
	private HashMap<Socket,Client> socketToClient	= new HashMap<Socket,Client>();
	private HashMap<String,Socket> namestToSocket	= new HashMap<String,Socket>();
	private HashMap<Integer,Client> portToClients 	= new HashMap<Integer,Client>();

	private HashSet<Client> userNames 				= new HashSet<Client>();
	private HashSet<Socket> sockets					= new HashSet<Socket>();
	private Vector<String> onlineUserList			= new Vector<String>();
	private Client c								= null;

	public synchronized void disconnectClient(int port, Throwable e, Broadcaster bc, Socket sock) {
		BroadCastMessage bcm = new BroadCastMessage();
		c = getClientByPort(port);
		if (c != null) {
			bcm.setOwnerLogin(c.getLogin());
			bcm.setOwnerName(c.getName());
			bcm.setText("SERVER> " + c.getLogin() +  " had a problem: " + e.getLocalizedMessage());
			bcm.setServresponse("SERVER> " + c.getLogin() +  " had a problem: " + e.getLocalizedMessage());
			removeClientByLoginAndPlatform(c.getLogin(), c.getPlatform());
		} else {
			bcm.setOwnerLogin("N/A");
			bcm.setOwnerName("N/A");
			bcm.setText("SERVER> A User had a problem: " + e.getLocalizedMessage());
			bcm.setServresponse("SERVER> A User had a problem: " + e.getLocalizedMessage());
		}
		bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
		try {
			bc.broadCastMessage(bcm);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.err.println("SERVER> Broadcaster exeption: " + e1.getLocalizedMessage());
		}

		// PASSES THE THROWN EXCEPTION TO THE CLIENT
		if (e instanceof ServerException) {
			if (((ServerException) e).isToDisconnect()) {
				try {
					SendObject so = new SendObject();
					so.send(sock, e);
					sock.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

	}

	public HashSet<Socket> getSockets() {
		return sockets;
	}

	public synchronized boolean checkNameAvaliability(String s) throws ServerException, SQLException {
		if (s.contains("@")) {
			s = DAO.getLoginByEmail(s);
		}
		return onlineUserList.contains(s);
	}

	public synchronized void addUser(Client c) throws ServerException {
		if (!userNames.add(c)) {
			throw new ServerException("Client login already in use: " + c.getLogin());
		}
		onlineUserList.add(c.toString());
	}

	public synchronized void addClient(Socket sock, Client c) throws Throwable {
		if (!loginsToClients.containsKey(c.getLogin() + c.getPlatform())) {
			loginsToClients.put(c.getLogin() + c.getPlatform(), c);
			sockets.add(sock);
			userNames.add(c);
			onlineUserList.add(c.toString());
			socketToClient.put(sock, c);
			portToClients.put(c.getLocalPort(), c);
			namestToSocket.put(c.getLogin() + c.getPlatform(), sock);
		} else {
			ServerException se = new ServerException(ServerMain.getTimestamp() + " SERVER> The login " + c.getLogin() + " is already in use.", true);
			se.setToDisconnect(true);
			throw se;
		}
	}

	public synchronized void removeClientByClassAndSocket(Client c,Socket sock) throws Throwable {
		socketToClient.remove(sock);
		sockets.remove(sock);
		userNames.remove(c);
		for (int i = 0; i < loginsToClients.size(); i++) {
			loginsToClients.remove(c.getLogin() + c.getPlatform());
		}
		for (int i = 0; i < onlineUserList.size(); i++) {
			onlineUserList.remove(c.toString());
		}
		for (int i = 0; i <= portToClients.size(); i++) {
			portToClients.remove(c.getLocalPort());
		}
		removeDoubleEntries(c);
	}

	public synchronized void removeClientBySocket(Socket sock) {
		Client c = socketToClient.get(sock);
		socketToClient.remove(sock);
		sockets.remove(sock);
		userNames.remove(c);
		for (int i = 0; i < loginsToClients.size(); i++) {
			loginsToClients.remove(c.getLogin() + c.getPlatform());
		}
		for (int i = 0; i < onlineUserList.size(); i++) {
			onlineUserList.remove(c.toString());
		}
		for (int i = 0; i <= portToClients.size(); i++) {
			portToClients.remove(c.getLocalPort());
		}
		removeDoubleEntries(c);
	}

	public synchronized void removeDoubleEntries(Client c) {
		for (String string : onlineUserList) {
			if (string.equalsIgnoreCase(c.toString())) {
				onlineUserList.remove(string);
			}
		}
	}

	public synchronized void removeClientByLoginAndPlatform(String login, String platform) {
		if(loginsToClients.containsKey(login + platform)) {
			c = loginsToClients.get(login + platform);
			socketToClient.remove(c);
			userNames.remove(c);
		}
		Socket socket = namestToSocket.get(login + platform);
		if (socket != null) {
			sockets.remove(socket);
			portToClients.remove(socket.getPort());
			namestToSocket.remove(socket);
		}
		for (int i = 0; i < loginsToClients.size(); i++) {
			loginsToClients.remove(c.getLogin() + c.getPlatform());
		}
		for (int i = 0; i < onlineUserList.size(); i++) {
			onlineUserList.remove(c.toString());
		}
	}

	public synchronized Client getClientByPort(Integer i) {
		Client c = null;
		if(portToClients.containsKey(i)) {
			return portToClients.get(i);
		} else {
			try {
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
			sockets.remove(namestToSocket.get(c.getLogin() + c.getPlatform()));
			namestToSocket.remove(c.getLogin() + c.getPlatform());
			userNames.remove(c);
			loginsToClients.remove(c.getLogin() + c.getPlatform());
			onlineUserList.remove(c.getLogin() + c.getPlatform());
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
		return userNames;
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
