package sendable;

public class ConnectionMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9104401230356460389L;

	@Override
	public String toString() {
		return "[" + this.getTimestamp() + "] " + this.getOwner() + " -> " + "Conected.";
	}

}
