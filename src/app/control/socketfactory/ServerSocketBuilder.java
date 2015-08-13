package app.control.socketfactory;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerSocketBuilder {

	public static ServerSocket jsock = null;
	private static int p; 

	public static ServerSocket createSocket() {
		try {
			if (jsock != null) {
				ServerSocketBuilder.jsock.close();
				ServerSocketBuilder.jsock = new ServerSocket(p);
			} else {
				ServerSocketBuilder.jsock = new ServerSocket(p);
				ServerSocketBuilder.jsock.setSoTimeout(0);
			}
		} catch (java.net.BindException e) {
			System.err.println("SERVER> " + e.getLocalizedMessage());
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			System.exit(1);
		} catch(IOException e) {
			System.err.println("Failed to open port. Retrying...");
			try {
				ServerSocketBuilder.jsock = new ServerSocket(p);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
//			System.exit(1);
		}
		return ServerSocketBuilder.returnSocket();
	}
	
	public static void dumpSocket() throws IOException {
		if (ServerSocketBuilder.jsock != null) {
			ServerSocketBuilder.jsock.close();
		}
		ServerSocketBuilder.jsock = null;
	}

	public static ServerSocket returnSocket() {
		if (ServerSocketBuilder.jsock != null) {
			return ServerSocketBuilder.jsock;
		} else {
			return null;
		}
	}

	public ServerSocketBuilder(int p) {
		ServerSocketBuilder.p = p;
	}

}
