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
	private Integer port;

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
							((NormalMessage)o).setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
							bc.broadCastMessage((Message)o);
						} else {
							throw new ServerException(getTimestamp() + " SERVER> Message greater than 100 characters.");
						}

					} else if (o instanceof DisconnectionMessage) {
						DisconnectionMessage dm = (DisconnectionMessage)o;
						BroadCastMessage bcm = new BroadCastMessage();
						bcm.setOwner(dm.getOwner());
						bcm.setText("Disconnected");
						bcm.setServresponse("SERVER> Disconnected");
						ServerMessage sm = new ServerMessage(ClientCenter.getInstance().getUsersNames());
						bc.broadCastMessage(sm);
						System.out.println(((DisconnectionMessage)o).toString());					
						ClientCenter.getInstance().removeClientByName(dm.getOwner());			
						bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
						bc.broadCastMessage(bcm);		
						sock.close();
						break;
					} else if (o instanceof ConnectionMessage) {
						((ConnectionMessage)o).setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
						so.send(sock, new ServerMessage("Online"));
					} 
				} else if (o instanceof Client) {
					Client c = (Client)o;
					c.setLocalPort(port);
					if (c.getVersion() == ServerMain.VERSION) {
						if (c.getName().length() < 21) {
							BroadCastMessage bcm = new BroadCastMessage();
							bcm.setOwner(c.getName());
							cc.addClient(c.getSock(), c);
							bcm.setText("Connected");
							bcm.setServresponse("SERVER> Connected");
							bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
							bc.broadCastMessage(bcm);
							ServerMessage sm = new ServerMessage(ClientCenter.getInstance().getUsersNames());
							sm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
							//							sm.setAddUser(c.getName());
							bc.broadCastMessage(sm);
							System.out.println(getTimestamp() + c.toString() + " -> Connected");
						} else {
							throw new ServerException(getTimestamp() + " SERVER> Name greater than 20 characters.",true);
						}
					} else if (c.getVersion() < ServerMain.VERSION) {
						throw new ServerException(getTimestamp() + " SERVER> Version " + ServerMain.VERSION + " required. Download at https://ibm.biz/BdE5ww",true);
					} else if (c.getVersion() > ServerMain.VERSION) {
						throw new ServerException(getTimestamp() + " SERVER> Version " + ServerMain.VERSION + " required. Download at https://ibm.biz/BdE5ww",true);
					}
				}

			} catch (ServerException e) {
				try {
					System.err.println(e.getMessage());
					so.send(sock, e);
					Client c = ClientCenter.getInstance().getClientSockets().get(sock);
					try {
						ClientCenter.getInstance().removeClientByName(c.getName());
					} catch (Throwable e1) {
						System.err.println(e1.getMessage());
					}
					if (e.isToDisconnect()) {
						sock.close();
						break;
					}
				} catch (IOException e1) {

					System.err.println(getTimestamp() + "SERVER> Could not deliver this Exception: " + e.toString());
				}
			} catch (IOException e) {
				System.err.println(getTimestamp() + "SERVER> Client/Server Error disconnected unexpectedly.");
				Client c = ClientCenter.getInstance().getClientByPort(port);
				try {
					ClientCenter.getInstance().removeClientByPort(port);
				} catch (Throwable e2) {
					System.err.println(e2.getMessage());
				}

				BroadCastMessage bcm = new BroadCastMessage();
				bcm.setOwner(c.getName());
				bcm.setText("SERVER> " + c.getName() +  " had a connection error.");
				bcm.setServresponse("SERVER> " + c.getName() +  " had a connection error.");
				bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
				try {
					bc.broadCastMessage(bcm);
				} catch (IOException e1) {
					System.err.println(e1.getMessage());
				}
				/*try {
					so.send(sock, new DisconnectionMessage(true));
					try {
						sock.close();
					} catch (IOException e1) {
						Client c = ClientCenter.getInstance().getClientSockets().get(sock);
						try {
							ClientCenter.getInstance().removeClientByName(c.getName());
						} catch (Throwable e4) {
							System.err.println(e1.getMessage());
						}
					}
					Client c = ClientCenter.getInstance().getClientSockets().get(sock);
					try {
						ClientCenter.getInstance().removeClientByName(c.getName());
					} catch (Throwable e1) {
						System.err.println(e1.getMessage());
					}
					sock.close();*/
				/*} catch (IOException e1) {
					try {
						Client c = ClientCenter.getInstance().getClientSockets().get(sock);
						try {
							ClientCenter.getInstance().removeClientByName(c.getName());
						} catch (Throwable e6) {
							System.err.println(e1.getMessage());
						}
						sock.close();
					} catch (IOException e2) {
					}
					Client c = ClientCenter.getInstance().getClientSockets().get(sock);
					try {
						ClientCenter.getInstance().removeClientByName(c.getName());
					} catch (Throwable e3) {
//						System.err.println(e1.getMessage());
					}
					break;
				} finally {
					try {
						Client c = ClientCenter.getInstance().getClientSockets().get(sock);
						try {
							ClientCenter.getInstance().removeClientByName(c.getName());
						} catch (Throwable e1) {
							System.err.println(e1.getMessage());
						}
						sock.close();
					} catch (IOException e1) {
					}
					Client c = ClientCenter.getInstance().getClientSockets().get(sock);
					try {
						ClientCenter.getInstance().removeClientByName(c.getName());
					} catch (Throwable e1) {
//						System.err.println(e1.getMessage());
					}
					sock = null;
					break;*/
	
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (Throwable e) {
				System.err.println(e.getMessage());
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
		this.port = sock.getPort();
	}
}