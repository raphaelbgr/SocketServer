package sendable;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerMessage extends Message implements Serializable {

	/**
	 * For server background communication purposes.
	 */
	private static final long serialVersionUID = 3772354662147009097L;
	
	private ArrayList [] clist = null;

	public ServerMessage buildMessage(String s) {
		setServresponse(s);
		setTimestamp();
		return this;
	}
	
	public ServerMessage(String s) {
		super();
		setServresponse(s);
		setTimestamp();
	}
	
	public ServerMessage() {
		super();
	}
	
/*	public ServerMessage(Client [] clist) {
		super();
		setClist(clist);
	}*/
	
	public ServerMessage(ArrayList[] clist) {
		super();
		setClist(clist);
	}

	@Override
	public String toString() {
		return "[" + this.getTimestamp() + "]" + " " + "SERVER> " + this.getServresponse();
	}

	public Object[] getClist() {
		return clist;
	}

	public void setClist(ArrayList[] clist) {
		this.clist = (ArrayList []) clist;
	}
}
