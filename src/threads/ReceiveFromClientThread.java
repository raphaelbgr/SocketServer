package threads;

import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import sendable.Client;
import sendable.ConnectionMessage;
import sendable.DisconnectionMessage;
import sendable.Message;
import sendable.NormalMessage;
import sendable.ServerMessage;

import communication.MessageHandler;
import communication.ReceiveObject;
import communication.SendObject;

import exceptions.ServerException;

public class ReceiveFromClientThread implements Runnable {
	Socket sock			= null;
	ReceiveObject ro 	= new ReceiveObject();
	SendObject so		= new SendObject();
	MessageHandler mh 	= new MessageHandler();
	
	public void run() {
		while(true) {
			try {
				Object o = ro.receive(sock);
				if (o instanceof Message) {
					if (o instanceof ServerMessage) {
						// No use for this yet.
					} else if (o instanceof NormalMessage) {
						((NormalMessage)o).setServresponse("SERVER> Received");
						so.send(sock, o);
					} else if (o instanceof DisconnectionMessage) {						
						System.out.println(((DisconnectionMessage)o).toString());
						sock.close();
						break;
					} else if (o instanceof ConnectionMessage) {
						so.send(sock, new ServerMessage("Online"));
					} else if (o instanceof Client) {
						so.send(sock, new ServerMessage("Online"));
					}
				}
				
			} catch (ServerException e) {
				try {
					so.send(sock, e);
				} catch (IOException e1) {
					System.err.println(getTimestamp() + "Could not deliver this Exception: " + e.toString());
				}
			} catch (IOException e) {
				System.err.println(getTimestamp() + "Client disconnected unexpectedly.");
				break;
			} catch (ClassNotFoundException e) {
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