package uk.co.marcoratto.net;

import java.net.SocketException;

public class ClientFTPException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8506246869537410210L;

	public ClientFTPException(String s) {
        super(s);
    }

	public ClientFTPException(Exception e) {
		super(e);
	}
}
