package servermain;

import java.util.ArrayList;
import java.util.List;

import connection.ConnectionSpecification;
import connection.LinkCommunication;

import sendable.Client;
import sync.MessageCenter;
import threads.ConnectionHandlerThread;

public class ServerMain {
	public final static int PORT = 21215;
	public static MessageCenter mc = new MessageCenter();
	@SuppressWarnings("unused")
	private List<Client> clientList = new ArrayList<Client>(); 
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		ConnectionSpecification dao = LinkCommunication.getInstance();

		ConnectionHandlerThread ch = new ConnectionHandlerThread(PORT);
		
		Thread t1 = new Thread(ch);
		t1.start();
		System.out.println("SERVER> Listening on port " + PORT);
	}
}
