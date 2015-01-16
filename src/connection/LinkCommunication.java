package connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Calendar;

import sendable.Message;
import servermain.ServerMain;
import sync.Broadcaster;
import sync.ClientCenter;

public class LinkCommunication implements ConnectionSpecification {

	private ObjectInputStream iis = null;
	private static LinkCommunication dao = null;
	private InputStream is = null;
	private Socket link;


	private LinkCommunication() {
	}

	public ObjectInputStream assembleObjectInputStream(InputStream is) throws IOException {
		return new ObjectInputStream(is);
	}

	public void setObjectInputStream(ObjectInputStream iis) {
		this.iis = iis;
	}

	public ObjectInputStream getObjectInputStream() {
		return this.iis;
	}

	public void setInputStream(InputStream is) {
		this.is = is;
	}

	public InputStream getInputStream() {
		return this.is;
	}

	public void setSocket(Socket link) {
		this.link = link;
	}

	public Socket getSocket() {
		return this.link;
	}

	public boolean send(Message m) {
		return false;
	}

	public synchronized Message receiveNormalMessage(Message m) {
		m.setIp(link.getInetAddress().getHostAddress());
		if(link.isClosed() | link.isInputShutdown() | link.isOutputShutdown()) {
			try {
				System.err.println("[" + m.getTimestamp() + "] " + "[Origin: " + m.getIp() + "] " + m.getOwner() + " Disconnected/Timed out." );
				this.finalize();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("[" + m.getTimestamp() + "] " + m.getOwner() + " -> " + m.getText());
		}
		return m;
	}

	public void initializeSocket() {
		if (link == null) {
			this.link = getSocket();
		}
	}

	public synchronized Message receive() throws Throwable {
		Message m = null;
		if (this.link != null && this.link.isConnected()) {
			m = (Message) this.getObjectInputStream().readObject();
			m.setIp(link.getInetAddress().getHostAddress());
			if((this.link.getInetAddress().isLinkLocalAddress())) {
				m.setNetwork(1);
			} else if (this.link.getInetAddress().isLoopbackAddress()) {
				m.setNetwork(0);
			} else {
				m.setNetwork(2);
			}
			if (m.getType().equalsIgnoreCase("normal")) {	
				m = (Message) receiveNormalMessage(m);
				ObjectOutputStream oos = new ObjectOutputStream(link.getOutputStream());
				m.setServresponse("Delivered to server.");
				Broadcaster b = new Broadcaster();								//BROADCASTS THE MESSAGE!!!!
				b.broadCastMessage(m);
				try {
					m.setType("");
					oos.writeObject(m);
				} catch (IOException e) {
					System.err.println("Could not deliver response to client:" + m.getOwner());
				}
			} else if (m.getType().equalsIgnoreCase("connectreq")) {
				System.out.println("[" + m.getTimestamp() + "]" + "[" + m.getNetwork() + "] " + m.getOwner() + " connected from "+this.link.getInetAddress()+":"+this.link.getPort());
				ClientCenter.getInstance().addClient(link, m);
				ObjectOutputStream oos = new ObjectOutputStream(link.getOutputStream());
				m.setServresponse("Connected");
				m.setType("connectok");
				try {
					oos.writeObject(m);
					oos.flush();
				} catch (IOException e) {
					System.err.println("Could not deliver response to client:" + m.getOwner());
				}
			} else if (m.getType().equalsIgnoreCase("disconnect")) {
				System.err.println("[" + m.getTimestamp() + "]" + "[Origin: " + m.getIp() + "] " + m.getOwner() + " Disconnected." );
				ClientCenter.getInstance().removeClientByName(m.getOwner());

			}
		} else {
			return null;
		}
		return m;
	}

	public synchronized Message AssembleNormalMessage() {
		Message m = null;
		try {
			if (link.isConnected()) {
				m = (Message) this.getObjectInputStream().readObject();
				m.setTimestamp();
				m.setIp(link.getInetAddress().getHostAddress());
				m.setCreationtime(Calendar.getInstance().getTimeInMillis());
				if(m.isDisconnect() | link.isClosed() | link.isInputShutdown() | link.isOutputShutdown()) {
					try {
						System.err.println("[" + m.getTimestamp() + "] " + "[Origin: " + m.getIp() + "] " + m.getOwner() + " Disconnected." );
						this.finalize();
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
				System.out.println("[" + m.getTimestamp() + "] " + m.getOwner() + " -> " + m.getText());
				ServerMain.mc.pushMessageToList(m); //Adiciona a mensagem numa lista.
			}
		} catch (IOException | ClassNotFoundException e) {
		}
		return m;
	}

	public static LinkCommunication getInstance() {
		if (dao == null) {
			dao = new LinkCommunication();
		}
		return dao;
	}
}
