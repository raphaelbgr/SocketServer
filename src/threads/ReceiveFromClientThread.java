package threads;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
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
	Client localClient 	= null;
	String cName 		= null;

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
						bcm.setOwner(cName);
						bcm.setText("Disconnected");
						bcm.setServresponse("SERVER> Disconnected");
//						ClientCenter.getInstance().removeClientByName(dm.getOwner());
						ClientCenter.getInstance().removeClientByClass(localClient);
						ServerMessage sm = new ServerMessage(ClientCenter.getInstance().getUsersNames());
						bc.broadCastMessage(sm);
//						bc.broadCastMessage(dm);
						System.out.println(this.getTimestamp()+ cName + " -> " + "Disconnected");				

						bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
						bc.broadCastMessage(bcm);		
						sock.close();
						sock = null;
						break;
					} else if (o instanceof ConnectionMessage) {
						((ConnectionMessage)o).setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
						so.send(sock, new ServerMessage("Online"));
					} 
				} else if (o instanceof Client) {
					Client c = (Client)o;
					localClient = c;
					cName = c.getName();
					c.setLocalPort(port);
					if (c.getVersion() == ServerMain.VERSION) {
						if (cName.length() < 21) {
							if (!ClientCenter.getInstance().checkNameAvaliability(cName)) {
								BroadCastMessage bcm = new BroadCastMessage();
								bcm.setOwner(cName);
								cc.addClient(c.getSock(), c);
								bcm.setText("Connected");
								bcm.setServresponse("SERVER> Connected");
								bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
								bc.broadCastMessage(bcm);
								ServerMessage sm = new ServerMessage(ClientCenter.getInstance().getUsersNames());
								sm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
								bc.broadCastMessage(sm);
								System.out.println(getTimestamp() + c.toString() + " -> Connected");
								
								ServerMessage smConnect = new ServerMessage();
								smConnect.setConnect(true);
								smConnect.setServresponse("Welcome " + cName);
								so.send(sock, smConnect);
							} else {
								throw new ServerException(getTimestamp() + "SERVER> The name " + cName + " is already in use.",true, true);
							}			
						} else {
							throw new ServerException(getTimestamp() + " SERVER> Name greater than 20 characters.",true);
						}
					} else if (c.getVersion() < ServerMain.VERSION) {
						throw new ServerException(getTimestamp() + " SERVER> Version " + ServerMain.VERSION + " required. DL at https://ibm.biz/BdE5ww",true);
					} else if (c.getVersion() > ServerMain.VERSION) {
						throw new ServerException(getTimestamp() + " SERVER> Version " + ServerMain.VERSION + " required. DL at https://ibm.biz/BdE5ww",true);
					}
				}

			} catch (EOFException e){ 
					try {
						sock.close();
					} catch (IOException e1) {
						
						e1.printStackTrace();
					} finally {
						sock = null;
						
						try {
							ClientCenter.getInstance().removeClientByName(cName);
						} catch (Throwable e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							break;
						}
						break;
					}
					
			}
			catch (SocketException e) {
//				e.printStackTrace();
				System.err.println(getTimestamp() + "SERVER> " + cName + " had a connection problem.");
				try {
					sock.close();
					sock = null;
					ClientCenter.getInstance().removeClientByName(cName);
				} catch (Throwable e1) {
					// TODO Auto-generated catch block
//					e1.printStackTrace();
				}
				break;
			}
			catch (ServerException e) {
				try {
					System.err.println(e.getMessage());
					so.send(sock, e);
//					Client c = ClientCenter.getInstance().getClientSockets().get(sock);
					if (!e.isDoubleName()) {
						try {
							ClientCenter.getInstance().removeClientByName(cName);
						} catch (Throwable e1) {
							System.err.println(e1.getMessage());
						}
					}
					if (e.isToDisconnect()) {
						sock.close();
						break;
					}
				} catch (IOException e1) {
					System.err.println(getTimestamp() + "SERVER> Could not deliver this Exception: " + e.toString());
				}
			} catch (Throwable e) {
				e.printStackTrace();
				Client c = localClient;
				if (c != null) {
					System.out.println(getTimestamp() + "SERVER> " + cName + " Disconnected.");
				} else {
					//					System.err.println(getTimestamp() + "SERVER> Client/Server Error disconnected unexpectedly.");
				}
				try {
					ClientCenter.getInstance().removeClientByName(cName);
				} catch (Throwable e3) {	
				}
				try {
					ClientCenter.getInstance().removeClientByPort(port);
					this.sock.close();
					sock = null;
					break;
				} catch (Throwable e2) {	
				} 
				finally {
//					this.sock = null;
					BroadCastMessage bcm = new BroadCastMessage();
					if (c != null) {
						bcm.setOwner(cName);
						bcm.setText("SERVER> " + cName +  " had a connection error.");
						bcm.setServresponse("SERVER> " + cName +  " had a connection error.");
					}
					bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
					try {
						bc.broadCastMessage(bcm);
					} catch (IOException e1) {
						//						System.err.println("Broadcast error.");
					}
				}
				break;
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