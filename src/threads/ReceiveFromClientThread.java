package threads;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.SocketException;

import connection.LinkCommunication;

import sendable.Message;
import sync.ClientCenter;
import clientserverside.Client;
import exceptions.ServerException;

public class ReceiveFromClientThread implements Runnable {

	LinkCommunication dao = LinkCommunication.getInstance();
	ObjectInputStream inFromClient = null;
	Socket link = null;
	Message m = null;
	Client c = null;
	ClientCenter cc = ClientCenter.getInstance();

	public void run() {
		dao.setSocket(link);
		try {
			dao.setInputStream(link.getInputStream());
			dao.setObjectInputStream(dao.assembleObjectInputStream(link.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while(true) {	
			try {
				if(dao.receive().isDisconnect()) {
					inFromClient = null;
					link = null;
					m = null;
					c = null;
					break;
				}
			} catch (ServerException se) {
				try {
					ObjectOutputStream oos = new ObjectOutputStream(link.getOutputStream());
					oos.writeObject(se);
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
			} catch (SocketException se1) {
				System.err.println("Client disconnected unexpectedly.");
				break;
			} catch (StreamCorruptedException e2) {
				System.err.println("Client shouldn't be allowed to send \"connect\" messages while conected. Connection killed.");
				inFromClient = null;
				link = null;
				m = null;
				c = null;
				break;
			} catch (EOFException e1) {
				System.err.println("Client shouldn't be allowed to send \"connect\". This thread is killed.");
				inFromClient = null;
				link = null;
				m = null;
				c = null;
				break;
			} catch (Throwable e) {
				e.printStackTrace();
				inFromClient = null;
				link = null;
				m = null;
				c = null;
				break;
			} finally {
				
			}
		}
	}

	public ReceiveFromClientThread(Socket link) {
		this.link = link;	
	}
}