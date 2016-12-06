package app.control.services;

import java.io.IOException;
import java.net.Socket;

import net.sytes.surfael.api.model.clients.Client;
import net.sytes.surfael.api.model.clients.NewClient;
import net.sytes.surfael.api.model.exceptions.ServerException;
import net.sytes.surfael.api.model.messages.DisconnectionMessage;
import net.sytes.surfael.api.model.messages.Message;
import net.sytes.surfael.api.model.messages.NormalMessage;
import net.sytes.surfael.api.model.messages.ServerMessage;
import app.ServerMain;
import app.control.communication.MessageHandler;
import app.control.communication.ReceiveObject;
import app.control.communication.SendObject;
import app.control.dao.DAO;
import app.control.services.receiver.ReceiverInterface;
import app.control.services.receiver.types.ClientReceiver;
import app.control.services.receiver.types.DisconnectionMessageReceiver;
import app.control.services.receiver.types.NormalMessageReceiver;
import app.control.services.receiver.types.RegistrationMessageReceiver;
import app.control.services.receiver.types.ServerMessageProcessor;
import app.control.sync.Broadcaster;
import app.control.sync.ClientCenter;

public class ReceiverManager implements Runnable {
	Socket sock			= null;
	ReceiveObject ro 	= new ReceiveObject();
	SendObject so		= new SendObject();
	MessageHandler mh 	= new MessageHandler();
	ClientCenter cc		= ClientCenter.getInstance();
	Broadcaster bc		= new Broadcaster();
	private Integer port;
	Client localClient 	= null;
	
	ReceiverInterface receiver = null;

	public void run() {
		while(true) {
			try {
				Object o = ro.receive(sock);
				if (o instanceof Message) {
					if (o instanceof NormalMessage) {
						receiver = new NormalMessageReceiver();
						receiver.receive(o,localClient,null);
					} else if (o instanceof DisconnectionMessage) {
						receiver = new DisconnectionMessageReceiver();
						receiver.receive(o,localClient,sock);
						break;
					} else if (o instanceof ServerMessage) {
						receiver = new ServerMessageProcessor();
						receiver.receive(o,localClient,sock);
					}
				} else if (o instanceof Client) {
					
					// In case of a user Proper Registration
					if (o instanceof NewClient) {
						receiver = new RegistrationMessageReceiver();
						receiver.receive(o, null, sock);
						break;
						
					// In case of a login wich auto-registration for a new comer *remove on the future*
					} else if (!DAO.clientExists((Client) o)) {
						receiver = new RegistrationMessageReceiver();
						receiver.receive(o, null, sock);
					}
					
					// Client login process
					Client c = (Client) o;
					if (DAO.doLogin(c)) {
						localClient = DAO.loadClientData((Client) o);
						localClient.setLocalPort(sock.getPort());
						receiver = new ClientReceiver();
						receiver.receive(o, localClient, sock);
					} else {
						if (c.getLogin() != null) {
							throw new ServerException(ServerMain.getTimestamp() + " SERVER> " +
									"Login " + c.getLogin() + " or/and Passwords doesn't match", true);
						} else {
							throw new ServerException(ServerMain.getTimestamp() + " SERVER> " +
									"Email " + c.getEmail() + " or/and Password doesn't match", true);
						}
					}
				}
			} catch (ServerException e) {
				e.printStackTrace();
				if (e.isToDisconnect()) {
					ClientCenter.getInstance().disconnectClient(port, e, bc, sock);
					break;
				} else {
					tryResponse(e);
				}

			} catch (Exception e) {
				e.printStackTrace();
				ClientCenter.getInstance().disconnectClient(port, e, bc, sock);
				tryResponse(e);
				break;
			}
		}
	}

	public void tryResponse(Exception e) {
		try {
			new SendObject().send(sock, e);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public ReceiverManager(Socket sock) {
		this.sock = sock;
		this.port = sock.getPort();
	}
}