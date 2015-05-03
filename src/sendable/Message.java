package sendable;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

@SuppressWarnings("unused")
public class Message implements Serializable, Comparable<Message> {

	/**
	 * Object Serial Version to send via TCP and WriteFile
	 */
	private static final long serialVersionUID = -8905875007093416665L;

	private Set<String> receivedby = new HashSet<String>();

	private Date msg_Date;

	private int version;
	
	private long creationTime;
	private long serverReceivedtime;
	private ClientSeenTime [] cst;
	private Vector<String> onlineUserList;

	private boolean disconnect = false;
	private boolean error = false;
	private boolean connect = false;

	private String addUser;
	private String delUser;
	private String owner;
	private String text;
	private String timestamp;
	private String date;

	private String ip;
	private String port;
	private String pcname;
	private String delay;
	private String network;
	private String type;
	private String servresponse;

	private String aux1;
	private String aux2;
	private String aux3;
	private String aux4;

	public Set<String> getSeen() {
		return receivedby;
	}
	public void addSeen(String name) {
		this.receivedby.add(name);
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp() {
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateFormatted = formatter.format(getMsg_Date()); //Extrair para metodo na Mensagem
		this.timestamp = dateFormatted;
	}
	public String getDate() {
		return date;
	}
	public void setDate() {
		DateFormat formatter = new SimpleDateFormat("dd/M/yyyy");
		String dateFormatted = formatter.format(getMsg_Date()); //Extrair para metodo na Mensagem
		this.date = dateFormatted;
	}
	public int getVersion() {
		return version;
	}
	public Vector<String> getOnlineUserList() {
		return onlineUserList;
	}
	public boolean isConnect() {
		return connect;
	}
	public void setConnect(boolean connect) {
		this.connect = connect;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public void setOnlineUserList(Vector<String> listData) {
		this.onlineUserList = listData;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getIp() {
		return ip;
	}
	public void setAddUser(String addUser) {
		this.addUser = addUser;
	}
	public void setDelUser(String s) {
		this.delUser = s;
	}
	public String getAddUser() {
		return this.addUser;
	}
	public String getDelUser() {
		return this.delUser;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getPcname() {
		return pcname;
	}
	public void setPcname(String pcname) {
		this.pcname = pcname;
	}
	public String getDelay() {
		return delay;
	}
	public boolean isDisconnect() {
		return disconnect;
	}
	public void setDisconnect(boolean disconnect) {
		this.disconnect = disconnect;
	}
	public void setDelay(String delay) {
		this.delay = delay;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(int i) {
		if ( i == 0 ) {
			this.network = "Loopback";
		} else if (i == 1){
			this.network = "Local";
		} else {
			this.network = "Internet";
		}
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getServresponse() {
		return servresponse;
	}
	public void setServresponse(String servresponse) {
		this.servresponse = servresponse;
	}
	public long getCreationtime() {
		return creationTime;
	}
	public void setCreationtime(long creationtime) {
		this.creationTime = creationtime;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 		: ip.hashCode());
		result = prime * result + ((network == null) ? 0 	: network.hashCode());
		result = prime * result + ((owner == null) ? 0 		: owner.hashCode());
		result = prime * result + ((pcname == null) ? 0 	: pcname.hashCode());
		return result;
	}

	/**
	 * Sugestao do Daniel Oliveira, de comparacao de objeto mensagem, excelente.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Message))
			return false;
		Message other = (Message) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (network == null) {
			if (other.network != null)
				return false;
		} else if (!network.equals(other.network))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (pcname == null) {
			if (other.pcname != null)
				return false;
		} else if (!pcname.equals(other.pcname))
			return false;
		return true;
	}

	/**
	 * Sugestao do Daniel Olivera (Muito boa) de impressao direta do objeto via toString();
	 */
		@Override
	public String toString() {
		return "[" + this.getTimestamp() + "] " + this.getOwner() + " -> " + this.getText();
	}
	/*
	@Override
	public int compare(Message o1, Message o2) {
		return o1.getType().compareTo(o2.getType());
	}*/

	@Override
	public int compareTo(Message m) {
		if (this.getCreationtime() > m.getCreationtime()) {
			return 1;
		} else if (this.getCreationtime() < m.getCreationtime()){
			return -1;
		} else {
			return 0;
		}
	}
	//AUTOMATICALLY SETS ITS CREATION TIME
	public Message () {
		this.setCreationtime(Calendar.getInstance().getTimeInMillis());
		setTimestamp();
	}
	public Date getMsg_Date() {
		return new Date();
	}
	public void setMsg_Date(Date msg_Date) {
		this.msg_Date = msg_Date;
	}

	public Message buildDisconnectMessage() {
		this.setDisconnect(true);
		this.setOwner(getOwner());
		this.setCreationtime(Calendar.getInstance().getTimeInMillis());
		this.setIp(getIp());
		this.setType("disconnect");
		this.setPcname(getPcname());
		this.setTimestamp();
		this.setDate();
		return this;
	}
	
	public Message buildConnectMessage() {
		this.setOwner(getOwner());
		this.setCreationtime(Calendar.getInstance().getTimeInMillis());
		this.setType("connectreq");
		this.setPcname(getPcname());
		this.setTimestamp();
		this.setDate();
		return this;
	}

	public Message (String owner, String ip, String Message) {
		setOwner(owner);
		setText(Message);
		setIp(ip);
		setCreationtime(Calendar.getInstance().getTimeInMillis());
	}
	
	public Message (String owner, String Message) {
		setText(Message);
		setOwner(owner);
		setIp("No IP");
		setCreationtime(Calendar.getInstance().getTimeInMillis());
	}

	public Message (String Message) {
		setOwner("Anonymous Owner");
		setIp("No IP");
		setText(Message);
		setCreationtime(Calendar.getInstance().getTimeInMillis());
	}
}