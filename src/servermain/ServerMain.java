package servermain;

import java.util.ArrayList;
import java.util.List;

import clientserverside.Client;
import dao.DAO;
import dao.SocketDao;
import sync.MessageCenter;
import threads.ConnectionHandlerThread;

public class ServerMain {
	public final static int PORT = 21215;
	public static MessageCenter mc = new MessageCenter();
	@SuppressWarnings("unused")
	private List<Client> clientList = new ArrayList<Client>(); 
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		DAO dao = SocketDao.getInstance();

		ConnectionHandlerThread ch = new ConnectionHandlerThread(PORT);
		
		//DisplayMessageListServer dys = new DisplayMessageListServer();
		//dys.start();
		
		Thread t1 = new Thread(ch);
		t1.start();
		System.out.println("Waiting for connection...");
	}
}
