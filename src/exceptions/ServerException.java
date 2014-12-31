package exceptions;

public class ServerException extends Throwable {

	/**
	 * 	Serializable Exception
	 */
	private static final long serialVersionUID = -4931885893331292884L;
	String s = null;
	
	public ServerException(String s) {
		this.s = s;
	}
	
	public String getMessage() {
		return this.s;
	}
	
}
