package app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.control.services.ConnectionHandlerThread;
import app.control.sync.MessageCenter;

public class ServerMain {
	public static int PORT = 2000;

	public static final String VERSION = "0.9.21";
	public static final String CLIENT_LNK = "https://goo.gl/rvLH93";

	public static boolean DEBUG 				= false;
	public static boolean DB					= true;

	public static MessageCenter mc = new MessageCenter();

	public static String DATABASE_CRYPT_KEY 	= null;
	public static String DATABASE_LOGIN 		= null;
	public static String DATABASE_PASS 			= null;
	public static String DATABASE_ADDR 			= null;
	public static String DATABASE_PORT 			= null;
	public static String DATABASE_SCHEMA 		= null;
	public static String DATABASE_FULL_URL		= null;
	
	public static boolean RECEIVE_CONN			= true; 
	
	public static String COMPILATION_KEY 		= null;

	public static void main(String[] args) {

		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("-port") && Integer.valueOf(args[i + 1]) > 0 && Integer.valueOf(args[i + 1]) <= 65535) {
				PORT = Integer.valueOf(args[i+1]);
			} else if (args[i].equalsIgnoreCase("-dblogin")) {
				DATABASE_LOGIN = args[i+1];
			} else if (args[i].equalsIgnoreCase("-dbpass")) {
				DATABASE_PASS = args[i+1];
			} else if (args[i].equalsIgnoreCase("-dbkey")) {
				DATABASE_CRYPT_KEY = args[i+1];
			} else if (args[i].equalsIgnoreCase("-dbaddr")) {
				DATABASE_ADDR = args[i+1];
			} else if (args[i].equalsIgnoreCase("-dbport")) {
				DATABASE_PORT = args[i+1];
			} else if (args[i].equalsIgnoreCase("-dbalias")) {
				DATABASE_SCHEMA = args[i+1];
			} else if (args[i].equalsIgnoreCase("-compkey")) {
				COMPILATION_KEY = args[i+1];
			} else if (args[i].equalsIgnoreCase("-db")) {			
				if (args[i+1].equalsIgnoreCase("false") || args[i+1].equalsIgnoreCase("off")) {
					DB 				= false;
					DATABASE_CRYPT_KEY 	= "TEST";
					DATABASE_LOGIN 		= "TEST";
					DATABASE_PASS 		= "TEST";
					DATABASE_ADDR 		= "TEST";
					COMPILATION_KEY 	= "TEST";
				}
			} else if (args[i].equalsIgnoreCase("-debug")) {
				if (args[i+1].equalsIgnoreCase("true") || args[i+1].equalsIgnoreCase("on")) {
					DEBUG = true;
				}
			}
		}
		if (DATABASE_CRYPT_KEY != null && DATABASE_LOGIN != null && DATABASE_PASS != null && DATABASE_ADDR != null) {
			ConnectionHandlerThread ch = new ConnectionHandlerThread(PORT);
			Thread t1 = new Thread(ch);
			t1.start();
			System.out.println(getTimestamp() + " " + "SERVER> Listening on port " + PORT);
			DATABASE_FULL_URL = "jdbc:mysql://"+ ServerMain.DATABASE_ADDR + ":" + ServerMain.DATABASE_PORT + "/" + ServerMain.DATABASE_SCHEMA;
		} else {
			System.out.println("Missing or invalid arguments, usage -port, -dblogin, -dbpass, -dbkey, -dbaddr, -dbport, -dbalias and -compkey to input the correct parameters for reaching the database.");
		}

	}

	public static String getTimestamp() {
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateFormatted = formatter.format(new Date());
		return "[" + dateFormatted + "]";
	}
}
