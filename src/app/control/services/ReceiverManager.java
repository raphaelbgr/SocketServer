package app.control.services;

import java.net.Socket;

import com.google.gson.Gson;

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
import app.model.clients.Client;
import app.model.clients.NewClient;
import app.model.messages.DisconnectionMessage;
import app.model.messages.Message;
import app.model.messages.NormalMessage;
import app.model.messages.ServerMessage;

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
				if (o instanceof String) {
					Gson gson = new Gson();
					o = gson.fromJson((String) o, Object.class);
				}
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
					if (o instanceof NewClient) {
						receiver = new RegistrationMessageReceiver();
						receiver.receive(o, null, sock);
						break;
					}
					localClient = DAO.loadClientData((Client)o);
					localClient.setLocalPort(sock.getPort());
					receiver = new ClientReceiver();
					receiver.receive(o,localClient,sock);
				}
			} catch (Throwable e) {
				e.printStackTrace();
				ClientCenter.getInstance().disconnectClient(port, e, bc, sock);
				break;
			}
		}
	}

	public ReceiverManager(Socket sock) {
		this.sock = sock;
		this.port = sock.getPort();
	}
}