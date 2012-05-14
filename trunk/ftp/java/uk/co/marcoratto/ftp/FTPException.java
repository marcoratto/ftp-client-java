package uk.co.marcoratto.ftp;

import org.apache.log4j.Logger;

public class FTPException extends Exception {

	private static final long serialVersionUID = -5198553893293728839L;
	
	private static Logger logger = Logger.getLogger("uk.co.marcoratto.ftp");

	public FTPException(String s) {
		super(s);
	}

	public FTPException(Throwable t) {
		super(t);
		logger.error(t.getMessage(), t);
	}

	public FTPException(String s, Exception e) {
		super(s, e);
		logger.error(s, e);
	}

	public FTPException(Exception e) {
		super(e);
		logger.error(e.getMessage(), e);
	}
	
}
