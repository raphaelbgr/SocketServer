package app.control.socketfactory;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import app.control.services.ReceiverManager;
import app.control.sync.ClientCenter;


public class LinkBuilder {

	private Socket sock = null;
	
	public synchronized void buildLink(ServerSocket jsock) {

		try {
			this.sock = jsock.accept();
			ClientCenter.getInstance().getSockets().add(this.sock);
			ReceiverManager rc = new ReceiverManager(sock);
			Thread t2 = new Thread(rc);
			t2.start();
		} catch (SocketException e) {
			e.printStackTrace();
			System.out.println("Socket Ex! On class LinkBuilder Line 27");
			if (sock != null) {
				try {
					ClientCenter.getInstance().removeClientBySocket(sock);
				} catch (Throwable e1) {
					try {
						ServerSocketBuilder.jsock.close();
					} catch (IOException e2) {
						e2.printStackTrace();
					} finally {
						ServerSocketBuilder.jsock = null;
						try {
							ServerSocketBuilder.dumpSocket();
						} catch (IOException e3) {
							e3.printStackTrace();
						}
					}
					e1.printStackTrace();
				}
				finally {
					ServerSocketBuilder.createSocket();
				}
			}
		} catch (EOFException e) {
			System.out.println("EOF Ex! On class LinkBuilder Line 51");
			if (sock != null) {
				try {
					ClientCenter.getInstance().removeClientBySocket(sock);
				} catch (Throwable e1) {
					e1.printStackTrace();
					try {
						ServerSocketBuilder.jsock.close();
					} catch (IOException e2) {
						e2.printStackTrace();
					} finally {
						ServerSocketBuilder.jsock = null;
						try {
							ServerSocketBuilder.dumpSocket();
						} catch (IOException e3) {
							e3.printStackTrace();
						}
					}
				} finally {
					ServerSocketBuilder.createSocket();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
