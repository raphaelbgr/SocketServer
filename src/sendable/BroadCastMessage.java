package sendable;

public class BroadCastMessage extends Message {
	
	private static final long serialVersionUID = 371144752902514230L;

	/**
	 * 
	 */

	@Override
	public String toString() {
		return "[" + this.getTimestamp() + "] " + this.getOwner() + " -> " + "Connected";
	}
	
}