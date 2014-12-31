package exceptions;

import java.io.IOException;

public class TransmitExeption extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String s = null;
	IOException ioe = null;
	
	public TransmitExeption(String s) {
		this.s = s;
	}
	
	public TransmitExeption(IOException ioe, String s) {
		this.ioe = ioe;
		this.s = s;
	}
	
	public String getError() {
		return s;
	}
	
}
