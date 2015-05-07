package servermain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import sync.MessageCenter;
import threads.ConnectionHandlerThread;

public class ServerMain {
	public static int PORT = 2000;
	public static final int VERSION = 14;
	public static MessageCenter mc = new MessageCenter();

	public static void main(String[] args) {
		if (args.length == 0) {
			ConnectionHandlerThread ch = new ConnectionHandlerThread(PORT);
			Thread t1 = new Thread(ch);
			t1.start();
			System.out.println(getTimestamp() + " " + "SERVER> Listening on port " + PORT);
		} else if (args[0].equalsIgnoreCase("-p") && Integer.valueOf(args[1]) > 0 && Integer.valueOf(args[1]) <= 65535) {
			PORT = Integer.valueOf(args[1]);
			ConnectionHandlerThread ch = new ConnectionHandlerThread(PORT);
			Thread t1 = new Thread(ch);
			t1.start();
			System.out.println(getTimestamp() + " " + "SERVER> Listening on port " + PORT);
		} else {
			System.out.println("Invalid argument, usage -p followed by a valid port number.");
		}
	}

	public static String getTimestamp() {
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateFormatted = formatter.format(new Date());
		return "[" + dateFormatted + "]";
	}
}
