package uk.co.marcoratto.util;

import java.net.SocketException;

public class UtilityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8506246869537410210L;

	public UtilityException(String s) {
        super(s);
    }

	public UtilityException(Exception e) {
		super(e);
	}

	public UtilityException(Throwable t) {
		super(t);
	}
}
