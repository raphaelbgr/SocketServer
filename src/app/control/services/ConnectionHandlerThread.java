package app.control.services;

import app.ServerMain;
import app.control.socketfactory.LinkBuilder;
import app.control.socketfactory.ServerSocketBuilder;

public class ConnectionHandlerThread extends Thread {

	private int port;
	ServerSocketBuilder sh = null;
	LinkBuilder lh = null;
	
	public void run() {	

		sh = new ServerSocketBuilder(port);
		lh = new LinkBuilder();
		
		do {
			try {
				lh.buildLink(ServerSocketBuilder.createSocket());
				ServerMain.RECEIVE_CONN = true;
				while(ServerMain.RECEIVE_CONN) {
					lh.buildLink(ServerSocketBuilder.returnSocket());
				}
			} catch (Exception e) {
				ConnectionHandlerThread ch = new ConnectionHandlerThread(ServerMain.PORT);
				Thread t1 = new Thread(ch);
				t1.start();
				
				e.printStackTrace();
				break;
			}
//			catch (IOException e1) {
//				e1.printStackTrace();
//				try {
//					sh.dumpSocket();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			} 
			finally {
//				try {
//					Thread.sleep(300);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} finally {
//					
//				}
			}
		} while (!ServerMain.RECEIVE_CONN);
		
	}
	
	public ConnectionHandlerThread(int port) {
		this.port = port;
	}
}
