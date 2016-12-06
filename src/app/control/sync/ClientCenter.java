package app.control.sync;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import app.control.communication.SendObject;
import app.control.dao.DAO;
import net.sytes.surfael.api.model.clients.Client;
import net.sytes.surfael.api.model.exceptions.ServerException;
import net.sytes.surfael.api.model.messages.BroadCastMessage;



public class ClientCenter {

	private static ClientCenter cc 					= null;
	
	private HashMap<String,Client> loginsToClients 	= new HashMap<String,Client>();
	private HashMap<String,Socket> namestToSocket	= new HashMap<String,Socket>();
	
	private HashMap<Integer, Socket> socketToPorts	= new HashMap<Integer,Socket>();
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
			removeClientByLoginAndPlatform(c.getLogin(), c.getPlatform(), String.valueOf(c.getLocalPort()));
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

	public synchronized void addClient(Socket sock, Client c) throws Exception {
		if (!onlineUserList.contains(c.toString())) {
			onlineUserList.add(c.toString());
		}
		loginsToClients.put(c.getLogin() + c.getPlatform() + c.getLocalPort(), c);
		sockets.add(sock);
		userNames.add(c);
		portToClients.put(c.getLocalPort(), c);
		namestToSocket.put(c.getLogin() + c.getPlatform() + c.getLocalPort(), sock);
	}

	public synchronized void removeClientByClassAndSocket(Client c,Socket sock) throws Exception {
		socketToPorts.remove(c.getLocalPort());
		sockets.remove(sock);
		userNames.remove(c);
		for (int i = 0; i < loginsToClients.size(); i++) {
			loginsToClients.remove(c.getLogin() + c.getPlatform() + c.getLocalPort());
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

	public synchronized void removeClientByLoginAndPlatform(String login, String platform, String port) {
		if(loginsToClients.containsKey(login + platform + port)) {
			c = loginsToClients.get(login + platform + port);
			userNames.remove(c);
		}
		Socket socket = namestToSocket.get(login + platform + port);
		if (socket != null) {
			sockets.remove(socket);
			portToClients.remove(socket.getPort());
			namestToSocket.remove(socket);
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

}
