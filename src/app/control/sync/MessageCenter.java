package app.control.sync;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.model.messages.Message;

public class MessageCenter {

	DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
	
	private List<Message> messageList = new ArrayList<Message>();
	
	public synchronized List<Message> getMessageList() {
		return messageList;
	}

	public synchronized Message pullMessageFromList(){
		Collections.sort(messageList);
		return this.messageList.get(0);
	}
	
	public synchronized List<Message> sendMessageList() {
		return this.messageList;
	}
	
	@Deprecated
	public synchronized void printList() {
		System.out.println("======= Message List =======");
		for (Message messageObj : messageList) {
			String dateFormatted = this.formatter.format(messageObj.getMsgCreationDate());
			System.out.println("[" + dateFormatted + "] " + messageObj.getOwnerLogin() + "-> " + messageObj.getText());
		}
	}
	
	public synchronized boolean pushMessageToList(Message m) {
		Collections.sort(messageList);
		boolean success = messageList.add(m);
		return success;
	}
	
//	public synchronized Set<String> verifySeenStatus(Message m){
//		return m.getSeen();
//	}
//	
//	public synchronized void addSeenStatus(Message m){
//		m.addSeen(m.getOwner());
//	}
//	
//	public synchronized Set<String> getSeenStatus(Message m) {
//		return m.getSeen();
//	}
	
	public synchronized void sendToPersistList(Message m){
		
	}
	
	public synchronized Message [] retrieveFromPersistList(Message [] m){
		
		return m;
	}
	
}
