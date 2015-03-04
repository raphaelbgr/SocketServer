package sendable;

import java.io.Serializable;
import java.net.Socket;


public class Client implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 296589332172289191L;
	Long id;
	Long msgCount;
	
	private int localPort;
	private int version;
	
	String text 				= null;
	String name 				= null;
	String middlename 			= null;
	String lastname 			= null;
	String membertype 			= null;
	String membersince 			= null;
	String onlinetime 			= null;
	
	Message lastMessage 		= null;
	
	long lastconnected 			= 0L;
	long lastMessageTime 		= 0L;
	long messagesSent 			= 0L;
	
	Socket sock 				= null;
	
	String ip 					= null;
	Integer port 				= null;
	
	String aux1 				= null;
	String aux2 				= null;
	String aux3 				= null;
	String aux4 				= null;
	
	public Client(Socket sock) {
		this.sock = sock;
	}
	public Client() {
		
	}
	public Client (Socket sock, Message m) {
		this.sock 				= sock;
		this.text 				= m.getText();
		this.name 				= m.getOwner();
		this.membertype 		= m.getType();
		this.lastconnected 		= m.getCreationtime();
		this.lastMessage 		= m;
		this.lastconnected 		= m.getCreationtime();
	}
	public Client(String string) {
		this.name = string;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMsgCount() {
		return msgCount;
	}
	public void setMsgCount(Long msgCount) {
		this.msgCount = msgCount + 1L;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMiddlename() {
		return middlename;
	}
	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getMembertype() {
		return membertype;
	}
	public void setMembertype(String membertype) {
		this.membertype = membertype;
	}
	public String getMembersince() {
		return membersince;
	}
	public void setMembersince(String membersince) {
		this.membersince = membersince;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getOnlinetime() {
		return onlinetime;
	}
	public void setOnlinetime(String onlinetime) {
		this.onlinetime = onlinetime;
	}
	public long getLastconnected() {
		return lastconnected;
	}
	public void setLastconnected(long lastconnected) {
		this.lastconnected = lastconnected;
	}
	public Socket getSock() {
		return sock;
	}
	public void setSock(Socket sock) {
		this.sock = sock;
	}
	public Message getLastMessage() {
		return lastMessage;
	}
	public void setLastMessage(Message lastMessage) {
		this.lastMessage = lastMessage;
	}
	public long getLastMessageTime() {
		return lastMessageTime;
	}
	public void setLastMessageTime(long lastMessageTime) {
		this.lastMessageTime = lastMessageTime;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	public int getLocalPort() {
		return localPort;
	}
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	
}
