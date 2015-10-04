package app.control.services.receiver.types;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

import app.ServerMain;
import app.control.communication.SendObject;
import app.control.services.receiver.ReceiverInterface;
import app.model.clients.Client;
import app.model.exceptions.ServerException;
import app.model.messages.Message;
import app.model.messages.RegistrationMessage;

public class RegistrationMessageReceiver implements ReceiverInterface{

	@Override
	public void receive(Object o, Client localClient, Socket sock) throws IOException, ServerException, SQLException, Throwable {
		//Sends the DB key to decrypt passwords on DB
		RegistrationMessage rm = (RegistrationMessage) o;
		if (rm.getCompilationKey() != null && ((Message) o).getCompilationKey().equalsIgnoreCase(ServerMain.COMPILATION_KEY)) {
			if (rm.getVersion().equalsIgnoreCase(ServerMain.VERSION)) {
				rm.setDbCryptKey(ServerMain.DATABASE_CRYPT_KEY);
				rm.setDbAddr(ServerMain.DATABASE_FULL_URL);
				rm.setDbPass(ServerMain.DATABASE_PASS);
				rm.setDbUser(ServerMain.DATABASE_LOGIN);
				
				SendObject so = new SendObject();
				so.send(sock, rm);
				
				System.out.println(ServerMain.getTimestamp() + "SERVER -> Sent registration DB key to anonymous client with PC name: " + rm.getPcname() + ", IP: " + rm.getIp() + "  and DNS hostname:" + rm.getDnsHostName());
				
//				sock.close();
//				sock = null;
			} else {
				throw new ServerException(ServerMain.getTimestamp() + "SERVER> Version " + ServerMain.VERSION + " required. Download at https://goo.gl/jN2mzM",true);
			}
		} else {
			throw new ServerException(ServerMain.getTimestamp() + "SERVER> Version " + ServerMain.VERSION + " required. Download at https://goo.gl/jN2mzM",true);
		}
	}

}
