package sendable;

import java.io.Serializable;

public class ServerMessage extends Message implements Serializable {

	/**
	 * For server background communication purposes.
	 */
	private static final long serialVersionUID = 3772354662147009097L;

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
	
	@Override
	public String toString() {
		return "[" + this.getTimestamp() + "]" + " " + "SERVER> " + this.getServresponse();
	}
}
