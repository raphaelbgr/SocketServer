package app.control.socketfactory;

import java.io.IOException;
import java.net.ServerSocket;

import app.ServerMain;

public class ServerSocketBuilder {

	public static ServerSocket jsock = null;

	public static ServerSocket createSocket() {
		try {
			ServerSocketBuilder.jsock = new ServerSocket(ServerMain.PORT);
			ServerSocketBuilder.jsock.setSoTimeout(0);
		} catch (java.net.BindException e) {
			System.err.println("SERVER> " + e.getLocalizedMessage());
			System.exit(1);
		} catch(IOException e) {
			System.err.println("SERVER> " + e.getLocalizedMessage());
			System.exit(1);
		}
		return ServerSocketBuilder.returnSocket();
	}
	
	public static void dumpServerSocket() {
		if (ServerSocketBuilder.jsock != null) {
			try {
				ServerSocketBuilder.jsock.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				ServerSocketBuilder.jsock = null;
			}
		} else {
			ServerSocketBuilder.jsock = null;
		}
	}

	public static ServerSocket returnSocket() {
		if (ServerSocketBuilder.jsock != null) {
			return ServerSocketBuilder.jsock;
		} else {
			return null;
		}
	}
}
