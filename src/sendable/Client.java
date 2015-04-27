package sendable;

import java.io.Serializable;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Client implements Serializable {

	private static final long serialVersionUID = 296589332172289191L;
	
	Date registrationDate 		= null;
	Date lastOnline 			= null;
	Date lastMessageSent		= null;
	
	private int localPort;
	private int version;
	
	String text 				= null;
	String name 				= null;
	String middlename 			= null;
	String lastname 			= null;
	String membertype 			= null;

	String login				= null;
	String password				= null;
	String cryptoPassword		= null;
	String email				= null;
	
	Message lastMessage 		= null;
	List<Message> unSentMsgs	= null;
	
	Long id						= null;
	Long msgCount				= null;
	Long onlinetime 			= null;
	Long messagesSent 			= null;
	
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
		this.lastMessage 		= m;
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
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCryptoPassword() {
		return cryptoPassword;
	}
	public void setCryptoPassword(String cryptoPassword) {
		this.cryptoPassword = cryptoPassword;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public Date getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(Date membersince) {
		this.registrationDate = membersince;
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
	public Long getOnlinetime() {
		return onlinetime;
	}
	public void setOnlinetime(Long onlinetime) {
		this.onlinetime = onlinetime;
	}
	public Date getLastOnline() {
		return lastOnline;
	}
	public void setLastOnline() {
		this.lastOnline = Calendar.getInstance().getTime();
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
	public Date getLastMessageSent() {
		return lastMessageSent;
	}
	public void setLastMessageTime(Date lastMessageTime) {
		this.lastMessageSent = lastMessageTime;
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
