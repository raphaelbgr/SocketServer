package servermain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sendable.Client;
import sync.MessageCenter;
import threads.ConnectionHandlerThread;
//import connection.LinkCommunication;

public class ServerMain {
	public final static int PORT = 2000;
	public static MessageCenter mc = new MessageCenter();
	@SuppressWarnings("unused")
	private List<Client> clientList = new ArrayList<Client>(); 

	public static void main(String[] args) {
		ConnectionHandlerThread ch = new ConnectionHandlerThread(PORT);
		Thread t1 = new Thread(ch);
		t1.start();
		System.out.println(getTimestamp() + " " + "SERVER> Listening on port " + PORT);
	}

	public static String getTimestamp() {
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateFormatted = formatter.format(new Date());
		return "[" + dateFormatted + "]";
	}
}
