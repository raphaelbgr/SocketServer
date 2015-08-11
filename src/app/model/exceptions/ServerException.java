package app.model.exceptions;

public class ServerException extends Throwable {

	/**
	 * 	Serializable Exception
	 */
	private static final long serialVersionUID = -4931885893331292884L;
	private boolean toDisconnect = false;
	
	String s = null;
	private boolean doubleName;
	
	public ServerException(String s, boolean toDisconnect, boolean isDoubleName) {
		this.s = s;
		this.toDisconnect = toDisconnect;
		this.doubleName = isDoubleName;
	}
	
	public ServerException(String s, boolean toDisconnect) {
		this.s = s;
		this.toDisconnect = toDisconnect;
	}
	
	public ServerException(String s) {
		this.s = s;
	}
	
	public String getMessage() {
		return this.s;
	}
	
	@Override
	public String toString() {
		return this.s;
	}

	public boolean isToDisconnect() {
		return toDisconnect;
	}

	public void setToDisconnect(boolean toDisconnect) {
		this.toDisconnect = toDisconnect;
	}
	
	public boolean isDoubleName() {
		return doubleName;
	}
	
	public void setDoubleName(boolean set) {
		this.doubleName = set;
	}
	
}
