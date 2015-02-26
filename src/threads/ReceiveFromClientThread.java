package threads;

import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sendable.BroadCastMessage;
import sendable.Client;
import sendable.ConnectionMessage;
import sendable.DisconnectionMessage;
import sendable.Message;
import sendable.NormalMessage;
import sendable.ServerMessage;
import servermain.ServerMain;
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
					} else if (o instanceof NormalMessage) {
						if (((NormalMessage)o).getText().length() < 101) {
							((NormalMessage)o).setServresponse("SERVER> Received");
							bc.broadCastMessage((Message)o);
						} else {
							throw new ServerException(getTimestamp() + "SERVER> Message greater than 100 characters.");
						}
						
					} else if (o instanceof DisconnectionMessage) {
						DisconnectionMessage dm = (DisconnectionMessage)o;
						BroadCastMessage bcm = new BroadCastMessage();
						bcm.setOwner(dm.getOwner());
						bcm.setText("Disconnected");
						bcm.setServresponse("SERVER> Disconnected");
						bc.broadCastMessage(bcm);
						cc.removeClientByName(dm.getOwner());
						ServerMessage sm = new ServerMessage((ArrayList[])ClientCenter.getInstance().getUsersNames().toArray());
						bc.broadCastMessage(sm);
						System.out.println(((DisconnectionMessage)o).toString());
						sock.close();
						break;
					} else if (o instanceof ConnectionMessage) {
						so.send(sock, new ServerMessage("Online"));
					} 
				} else if (o instanceof Client) {
					Client c = (Client)o;
					if (c.getVersion() == ServerMain.VERSION) {
						if (c.getName().length() < 20) {
							System.out.println(c.toString() + " -> Connected");
							BroadCastMessage bcm = new BroadCastMessage();
							bcm.setOwner(c.getName());
							cc.addClient(c.getSock(), c);
							bcm.setText("Connected");
							bcm.setServresponse("SERVER> Connected");
							bc.broadCastMessage(bcm);
							for (Object client : ClientCenter.getInstance().getUsersNames().toArray()) {
								System.out.println(((Client)client).getName());
							}
							ServerMessage sm = new ServerMessage((ArrayList[])ClientCenter.getInstance().getUsersNames().toArray());
							bc.broadCastMessage(sm);
						} else {
							throw new ServerException(getTimestamp() + " SERVER> Name greater than 20 characters.",true);
						}
					} else if (c.getVersion() < ServerMain.VERSION) {
						throw new ServerException(getTimestamp() + "SERVER> Version " + ServerMain.VERSION + " required. Download at https://ibm.biz/BdE5ww",true);
					} else if (c.getVersion() > ServerMain.VERSION) {
						throw new ServerException(getTimestamp() + "SERVER> Version " + ServerMain.VERSION + " required. Download at https://ibm.biz/BdE5ww",true);
					}
				}

			} catch (ServerException e) {
				try {
					System.err.println(e.getMessage());
					so.send(sock, e);
					if (e.isToDisconnect()) {
						sock.close();
						break;
					}
				} catch (IOException e1) {
					System.err.println(getTimestamp() + "Could not deliver this Exception: " + e.toString());
				}
			} catch (IOException e) {
				System.err.println(getTimestamp() + "Client/Server Error disconnected unexpectedly.");
				try {
					so.send(sock, new DisconnectionMessage(true));
					sock.close();
				} catch (IOException e1) {
					break;
				} finally {
					try {
						sock.close();
					} catch (IOException e1) {
					}
					sock = null;
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