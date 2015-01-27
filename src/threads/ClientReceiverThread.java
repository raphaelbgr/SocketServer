package threads;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import communication.ReceiveObject;
import communication.SendObject;

import exceptions.ServerException;

public class ClientReceiverThread implements Runnable {
	Socket sock			= null;
	ReceiveObject ro 	= new ReceiveObject();
	SendObject so		= new SendObject();

	public void run() {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(sock.getInputStream());
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		while(true) {
			try {
				ro.receive(sock,ois);
			} catch (ServerException e) {
				try {
					so.send(sock, e);
				} catch (IOException e1) {
					System.err.println("Could not deliver this Exception: " + e.toString());
				}
			} catch (IOException e) {
//				e.printStackTrace();
				System.err.println("Client disconnected unexpectedly.");
				break;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public ClientReceiverThread(Socket sock) {
		this.sock = sock;	
	}
}