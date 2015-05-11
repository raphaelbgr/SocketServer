package threads;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import sendable.BroadCastMessage;
import sendable.Client;
import sendable.ConnectionMessage;
import sendable.DisconnectionMessage;
import sendable.Message;
import sendable.NormalMessage;
import sendable.RegistrationMessage;
import sendable.ServerMessage;
import servermain.ServerMain;
import sync.Broadcaster;
import sync.ClientCenter;

import communication.MessageHandler;
import communication.ReceiveObject;
import communication.SendObject;

import dao.DAO;
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
	String cLogin 		= null;

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
							((Message)o).setOwnerName(localClient.getName());
							((Message)o).setOwnerLogin(localClient.getLogin());
							((Message)o).setServerReceivedtime();
							System.out.println(getTimestamp() + localClient.toString() + " -> " + ((Message)o).getText());
							bc.broadCastMessage((Message)o);
							try {
								DAO.connect();
								DAO.storeMessage((NormalMessage)o);
								DAO.updateSentMsgs(((Message)o));
							} catch (SQLException e) {
								System.err.println(getTimestamp() + "SERVER> Could not store this message on the database.");
								e.printStackTrace();
							} finally {
								try {
									DAO.disconnect();
								} catch (SQLException e) {							
								}
							}

						} else {
							throw new ServerException(getTimestamp() + " SERVER> Message greater than 100 characters.");
						}

					} else if (o instanceof DisconnectionMessage) {
						DisconnectionMessage dm = (DisconnectionMessage)o;
						BroadCastMessage bcm = new BroadCastMessage();
						bcm.setOwnerLogin(localClient.getLogin());
						bcm.setOwnerName(localClient.getName());
						bcm.setText("Disconnected");
						bcm.setServresponse("SERVER> Disconnected");
						//						ClientCenter.getInstance().removeClientByName(dm.getOwner());
						ClientCenter.getInstance().removeClientByClass(localClient);
						ServerMessage sm = new ServerMessage(ClientCenter.getInstance().getUsersNames());
						bc.broadCastMessage(sm);
						//						bc.broadCastMessage(dm);
						System.out.println(this.getTimestamp()+ localClient.getName() + " -> " + "Disconnected");				

						bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
						bc.broadCastMessage(bcm);		
						sock.close();
						sock = null;
						break;
					} else if (o instanceof ConnectionMessage) {
						((ConnectionMessage)o).setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
						so.send(sock, new ServerMessage("Online"));
					} else if (o instanceof RegistrationMessage) {
						
						//Sends the DB key to decrypt passwords on DB
						RegistrationMessage rm = (RegistrationMessage) o;
						rm.setDbCryptKey(ServerMain.DATABASE_CRYPT_KEY);
						rm.setDbAddr(ServerMain.DATABASE_FULL_URL);
						rm.setDbPass(ServerMain.DATABASE_PASS);
						rm.setDbUser(ServerMain.DATABASE_LOGIN);
						so.send(sock, rm);
						System.out.println(this.getTimestamp() + "SERVER -> Sent registration DB key to anonymous client.");
						sock.close();
						sock = null;
						break;
					}
				} else if (o instanceof Client) {
					Client c = (Client)o;
					localClient = c;
					cLogin = c.getLogin();
					c.setLocalPort(port);
					if (c.getVersion() == ServerMain.VERSION) {
						if (cLogin.length() < 21) {
							DAO.connect();
							if (DAO.verifyClientPassword(localClient)) {
								if (!ClientCenter.getInstance().checkNameAvaliability(cLogin)) {
									//Gets the client data on the database
									localClient = DAO.loadClientData(c);

									BroadCastMessage bcm = new BroadCastMessage();
									bcm.setOwnerLogin(cLogin);
									bcm.setOwnerName(localClient.getName());

									cc.addClient(c.getSock(), localClient);
									bcm.setText("Connected");
									bcm.setServresponse("SERVER> Connected");
									bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
									bc.broadCastMessage(bcm);

									//Sends the list of conencted people
									ServerMessage sm = new ServerMessage(ClientCenter.getInstance().getUsersNames());
									sm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
									bc.broadCastMessage(sm);
									System.out.println(getTimestamp() + localClient.toString() + " -> Connected");

									//Tells the client to enter local online mode
									ServerMessage smConnect = new ServerMessage();
									smConnect.setConnect(true);
									smConnect.setServresponse("Welcome " + localClient.getName());

									//Sends the login confirmation to client
									so.send(sock, smConnect);

								} else {
									DAO.disconnect();
									throw new ServerException(getTimestamp() + "SERVER> The login " + cLogin + " is already in use.",true, true);
								}
								DAO.disconnect();
							} else throw new ServerException(getTimestamp() + " SERVER> Wrong Password.",true);
						} else {
							DAO.disconnect();
							throw new ServerException(getTimestamp() + " SERVER> Name greater than 20 characters.",true);
						}
					} else if (c.getVersion() < ServerMain.VERSION) {
						DAO.disconnect();
						throw new ServerException(getTimestamp() + " SERVER> Version " + ServerMain.VERSION + " required. DL at https://ibm.biz/BdE5ww",true);
					} else if (c.getVersion() > ServerMain.VERSION) {
						DAO.disconnect();
						throw new ServerException(getTimestamp() + " SERVER> Version " + ServerMain.VERSION + " required. DL at https://ibm.biz/BdE5ww",true);
					}
				}

			} catch (EOFException e){ 
				e.printStackTrace();
				BroadCastMessage bcm = new BroadCastMessage();
				bcm.setOwnerLogin(cLogin);
				bcm.setOwnerName(localClient.getName());
				bcm.setText("Disconnected");
				bcm.setServresponse("SERVER> SocketTimeoutException error");
				try {
					ClientCenter.getInstance().removeClientByClass(localClient);
					bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
					bc.broadCastMessage(bcm);
				} catch (Throwable e2) {
					try {
						bc.broadCastMessage(bcm);
					} catch (IOException e1) {
					}
				}
				System.err.println(getTimestamp() + "SERVER> " + localClient.getName() + " had a EOFException.");
				try {
					sock.close();
					sock = null;
				} catch (Throwable e1) {
				}
				break;
			}
			catch (SocketTimeoutException e) {
				e.printStackTrace();
				BroadCastMessage bcm = new BroadCastMessage();
				bcm.setOwnerLogin(cLogin);
				bcm.setOwnerName(localClient.getName());
				bcm.setText("Disconnected");
				bcm.setServresponse("SERVER> SocketTimeoutException error");
				try {
					ClientCenter.getInstance().removeClientByClass(localClient);
					bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
					bc.broadCastMessage(bcm);
				} catch (Throwable e2) {
					try {
						bc.broadCastMessage(bcm);
					} catch (IOException e1) {
					}
				}
				System.err.println(getTimestamp() + "SERVER> " + cLogin + " had a SocketTimeoutException.");
				try {
					sock.close();
					sock = null;
				} catch (Throwable e1) {
				}
				break;
			}
			catch (SocketException e) {
				e.printStackTrace();
				BroadCastMessage bcm = new BroadCastMessage();
				bcm.setOwnerLogin(cLogin);
				bcm.setOwnerName(localClient.getName());
				bcm.setText("Disconnected");
				bcm.setServresponse("SERVER> SocketTimeoutException error");
				try {
					ClientCenter.getInstance().removeClientByClass(localClient);
					bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
					bc.broadCastMessage(bcm);
				} catch (Throwable e2) {
					try {
						bc.broadCastMessage(bcm);
					} catch (IOException e1) {
					}
				}
				System.err.println(getTimestamp() + "SERVER> " + localClient.getName() + " had a SocketException.");
				try {
					sock.close();
					sock = null;
				} catch (Throwable e1) {
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
							ClientCenter.getInstance().removeClientByLogin(cLogin);
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
					System.out.println(getTimestamp() + "SERVER> " + cLogin + " Disconnected.");
				} else {
					//					System.err.println(getTimestamp() + "SERVER> Client/Server Error disconnected unexpectedly.");
				}
				try {
					ClientCenter.getInstance().removeClientByLogin(cLogin);
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
						bcm.setOwnerLogin(cLogin);
						bcm.setOwnerName(localClient.getName());
						bcm.setText("SERVER> " + cLogin +  " had a connection error.");
						bcm.setServresponse("SERVER> " + cLogin +  " had a connection error.");
					}
					bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
					try {
						bc.broadCastMessage(bcm);
					} catch (IOException e1) {
						//						System.err.println("Broadcast error.");
					}
				}
				try {
					DAO.disconnect();
				} catch (SQLException e1) {
					e.printStackTrace();
					System.err.println(getTimestamp() + "SERVER> SQL Database error.");
					try {
						sock.close();
						sock = null;
					} catch (Throwable r) {
					} finally {
						try {
							throw new ServerException(getTimestamp() + " SERVER> Database server is offline.");
						} catch (ServerException e2) {
							// TODO Auto-generated catch block
							//							e2.printStackTrace();
						}
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