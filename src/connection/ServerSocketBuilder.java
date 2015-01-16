package connection;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerSocketBuilder {

	private ServerSocket jsock = null;
	private int p; 

	public ServerSocket createSocket() {
		try {
			this.jsock = new ServerSocket(p);
		} catch(IOException e){
			System.err.println("Failed to open port.");
			e.printStackTrace();
			System.exit(1);
		}
		return this.returnSocket();
	}

	public ServerSocket returnSocket(){
		if (this.jsock!= null) {
			return this.jsock;
		} else {
			return null;
		}
	}

	public ServerSocketBuilder(int p) {
		this.p = p;
	}

}
