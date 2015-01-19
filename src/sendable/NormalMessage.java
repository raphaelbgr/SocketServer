package sendable;

public class NormalMessage extends Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5676316381020467805L;

	@Override
	public String toString() {
		return "[" + this.getTimestamp() + "] " + this.getOwner() + " -> " + this.getText();
	}
	
}
