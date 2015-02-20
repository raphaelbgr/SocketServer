package threads;

import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import sendable.BroadCastMessage;
import sendable.Client;
import sendable.ConnectionMessage;
import sendable.DisconnectionMessage;
import sendable.Message;
import sendable.NormalMessage;
import sendable.ServerMessage;
import sync.Broadcaster;
import sync.ClientCenter;

import communication.MessageHandler;
import communication.ReceiveObject;
import communication.SendObject;

import exceptions.ServerException;

public class ReceiveFromClientThread implements Runnable {
	Socket sock			= null;
	ReceiveObject ro 	= new ReceiveObject();
	SendObject so		= new SendObject();
	MessageHandler mh 	= new MessageHandler();
	ClientCenter cc		= ClientCenter.getInstance();
	Broadcaster bc		= new Broadcaster();

	@SuppressWarnings("finally")
	public void run() {
		while(true) {
			try {
				Object o = ro.receive(sock);
				if (o instanceof Message) {
					if (o instanceof ServerMessage) {
						// No use for this yet.
					} else if (o instanceof NormalMessage) {
						((NormalMessage)o).setServresponse("SERVER> Received");
//						so.send(sock, o);
						bc.broadCastMessage((Message)o);
					} else if (o instanceof DisconnectionMessage) {
						DisconnectionMessage dm = (DisconnectionMessage)o;
						
						BroadCastMessage bcm = new BroadCastMessage();
						bcm.setOwner(dm.getOwner());
						bcm.setText("Disconnected");
						
						bc.broadCastMessage(bcm);
						cc.removeClientByName(dm.getOwner());
						System.out.println(((DisconnectionMessage)o).toString());
//						so.send(sock, new DisconnectionMessage(true));
						sock.close();
						//TODO BROADCAST THE DISCONNECTION
//						Thread.currentThread().stop();
						break;
					} else if (o instanceof ConnectionMessage) {
						so.send(sock, new ServerMessage("Online"));
					} 
				} else if (o instanceof Client) {
					Client c = (Client)o;
					System.out.println(c.toString() + " -> Connected");
//					so.send(sock, new ServerMessage("Online, welcome " + c.getName()));
					BroadCastMessage bcm = new BroadCastMessage();
//					bcm.setServresponse("Broadcast received");
//					bcm.setText(c.toString() + " -> Connected");
					bcm.setOwner(c.getName());
					bcm.setText("Connected");
					bc.broadCastMessage(bcm);
					cc.addClient(c.getSock(), c);
					//TODO FINISH BROADCAST NOT WORKING
				}

			} catch (ServerException e) {
				try {
					System.err.println(e.getMessage());
					so.send(sock, e);
					if (e.isToDisconnect()) {
//						so.send(sock, new DisconnectionMessage(true));
//						TODO SOCKET NEEDS TO CLOSE
						sock.close();
//						Thread.currentThread().stop();
						break;
					}
				} catch (IOException e1) {
					System.err.println(getTimestamp() + "Could not deliver this Exception: " + e.toString());
				}
			} catch (IOException e) {
				System.err.println(getTimestamp() + "Client/Server Error disconnected unexpectedly.");
				try {
					so.send(sock, new DisconnectionMessage(true));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					//e1.printStackTrace();
					break;
				} finally {
//					Thread.currentThread().stop();
					break;
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.getMessage();
				e.printStackTrace();
			} finally {
			}
		}
	}

	private String getTimestamp() {
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateFormatted = formatter.format(new Date());
		return "["+dateFormatted+"]" + " ";
	}

	public ReceiveFromClientThread(Socket sock) {
		this.sock = sock;	
	}
}