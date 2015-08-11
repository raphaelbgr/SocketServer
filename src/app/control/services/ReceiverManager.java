package app.control.services;

import java.io.EOFException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.control.communication.MessageHandler;
import app.control.communication.ReceiveObject;
import app.control.communication.SendObject;
import app.control.dao.DAO;
import app.control.services.receiver.ReceiverInterface;
import app.control.services.receiver.types.ClientReceiver;
import app.control.services.receiver.types.DisconnectionMessageReceiver;
import app.control.services.receiver.types.NormalMessageReceiver;
import app.control.services.receiver.types.RegistrationMessageReceiver;
import app.control.socketfactory.ServerSocketBuilder;
import app.control.sync.Broadcaster;
import app.control.sync.ClientCenter;
import app.model.clients.Client;
import app.model.clients.WebClient;
import app.model.exceptions.ServerException;
import app.model.messages.BroadCastMessage;
import app.model.messages.DisconnectionMessage;
import app.model.messages.Message;
import app.model.messages.NormalMessage;
import app.model.messages.RegistrationMessage;

public class ReceiverManager implements Runnable {
	Socket sock			= null;
	ReceiveObject ro 	= new ReceiveObject();
	SendObject so		= new SendObject();
	MessageHandler mh 	= new MessageHandler();
	ClientCenter cc		= ClientCenter.getInstance();
	Broadcaster bc		= new Broadcaster();
	private Integer port;
	Client localClient 	= null;
	String cLogin 		= null;
	
	ReceiverInterface receiver = null;

	public synchronized void synchedReceive() {
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
					} else if (o instanceof RegistrationMessage) {
						receiver = new RegistrationMessageReceiver();
						receiver.receive(o,localClient,sock);
						break;
					}
				} else if (o instanceof Client) {
					Client c = (Client)o;
					c.setLocalPort(sock.getPort());
					cLogin = c.getLogin();
					localClient = c;
					if (o instanceof WebClient) {
						//TODO WEBCLIENT OBJ
					} else {
						receiver = new ClientReceiver();
						receiver.receive(o,localClient,sock);
					}
				}
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
				BroadCastMessage bcm = new BroadCastMessage();
				bcm.setOwnerLogin(cLogin);
				bcm.setOwnerName(localClient.getName());
				bcm.setText("Disconnected");
				bcm.setServresponse("SERVER> SocketTimeoutException error");
				try {
					ClientCenter.getInstance().removeClientByClassAndSocket(localClient, sock);
					bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
					bc.broadCastMessage(bcm);
				} catch (Throwable e2) {
					try {
						bc.broadCastMessage(bcm);
					} catch (IOException e1) {
						
					}
				} finally {
					try {
						ServerSocketBuilder.dumpSocket();
						ServerSocketBuilder.createSocket();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				System.err.println(getTimestamp() + "SERVER> " + localClient.getName() + " had a EOFException.");
				try {
					sock.close();
					sock = null;
				} catch (Throwable e1) {
					
				}
				break;
			} catch (EOFException e) {
				e.printStackTrace();
				BroadCastMessage bcm = new BroadCastMessage();
				bcm.setOwnerLogin(cLogin);
				bcm.setOwnerName(localClient.getName());
				bcm.setText("Disconnected");
				bcm.setServresponse("SERVER> SocketTimeoutException error");
				try {
//					ClientCenter.getInstance().removeClientByClass(localClient);
					ClientCenter.getInstance().removeClientByClassAndSocket(localClient, sock);
					bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
					bc.broadCastMessage(bcm);
				} catch (Throwable e2) {
					try {
						bc.broadCastMessage(bcm);
					} catch (IOException e1) {
						
					}
				} finally {
					try {
						ServerSocketBuilder.dumpSocket();
						ServerSocketBuilder.createSocket();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				System.err.println(getTimestamp() + "SERVER> " + localClient.getName() + " had a EOFException.");
				try {
					sock.close();
					sock = null;
				} catch (Throwable e1) {
					
				}
				break;
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				BroadCastMessage bcm = new BroadCastMessage();
				bcm.setOwnerLogin(cLogin);
				bcm.setOwnerName(localClient.getName());
				bcm.setText("Disconnected");
				bcm.setServresponse("SERVER> SocketTimeoutException error");
				try {
//					ClientCenter.getInstance().removeClientByClass(localClient);
					ClientCenter.getInstance().removeClientByClassAndSocket(localClient, sock);
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
					
				} finally {
					try {
						ServerSocketBuilder.dumpSocket();
						ServerSocketBuilder.createSocket();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				break;
			} catch (SocketException e) {
				e.printStackTrace();
				BroadCastMessage bcm = new BroadCastMessage();
				if (localClient != null && localClient.getName() != null && cLogin != null) {
					bcm.setOwnerLogin(cLogin);
					bcm.setOwnerName(localClient.getName());
				}
				bcm.setText("Disconnected");
				bcm.setServresponse("SERVER> SocketTimeoutException error");
				try {
//					ClientCenter.getInstance().removeClientByClass(localClient);
					ClientCenter.getInstance().removeClientByClassAndSocket(localClient, sock);
					bcm.setOnlineUserList(ClientCenter.getInstance().getOnlineUserList());
					bc.broadCastMessage(bcm);
				} catch (Throwable e2) {
					try {
						bc.broadCastMessage(bcm);
					} catch (IOException e1) {
					}
				}
				if (localClient != null) {
					System.err.println(getTimestamp() + "SERVER> " + localClient.getName() + " had a SocketException.");
				}
				try {
					sock.getOutputStream().close();
					sock.getInputStream().close();
					sock.close();
					sock = null;
				} catch (Throwable e1) {
					
				} finally {
					try {
						ServerSocketBuilder.dumpSocket();
						ServerSocketBuilder.createSocket();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				break;
			} catch (ServerException e) {
				try {
					if (e.getMessage() != null) {
						System.err.println(e.getMessage());
					}
					so.send(sock, e);
					if (!e.isDoubleName()) {
						
					}
					if (e.isToDisconnect()) {
						sock.close();
						break;
					}
				} catch (IOException e1) {
					System.err.println(getTimestamp() + "SERVER> Could not deliver this Exception: " + e.toString());
				} finally {
					
				}
			} catch (Throwable e) {
				e.printStackTrace();
				Client c = localClient;
				if (c != null) {
					System.out.println(getTimestamp() + "SERVER> " + cLogin + " Disconnected.");
				} else {
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
					try {
						sock.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					sock = null;
				} 
				finally {
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
						} finally {
							try {
								ServerSocketBuilder.dumpSocket();
								ServerSocketBuilder.createSocket();
							} catch (IOException e2) {
								// TODO Auto-generated catch block
								e2.printStackTrace();
							}
						}
					}
				}
				break;
			} finally {
				
			}
		}
	}
	
	public void run() {
		synchedReceive();
	}

	private String getTimestamp() {
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateFormatted = formatter.format(new Date());
		return "["+dateFormatted+"]" + " ";
	}

	public ReceiverManager(Socket sock) {
		this.sock = sock;
		this.port = sock.getPort();
	}
}