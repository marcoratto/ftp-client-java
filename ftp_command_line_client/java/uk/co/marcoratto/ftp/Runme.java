package uk.co.marcoratto.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import uk.co.marcoratto.ftp.FTPException;

public class Runme {

	private final static Logger logger = Logger.getLogger("uk.co.marcoratto.ftp");
	private static int retCode = -1;
	
	public static void main(String[] args) {
		Runme runme = new Runme();
		runme.runme(args);
		System.exit(retCode);
	}

	public void runme(String[] args) {
		try {
			this.execute(args);
		} catch (Throwable t) {
			logger.info("Call listener onError...");
			retCode = 2;
		}
		logger.info("retCode=" + retCode);
	}

	public void execute(String[] args) throws FTPException {

		logger.debug("main");
		boolean autoLogin = false;
		boolean passiveMode = false;
		File script = null;
		InputStream is = System.in;
		
		for (int j = 0; j < args.length; j++) {
			logger.debug("args[" + j + "]:" + args[j]);
			if (args[j].equalsIgnoreCase("-h") == true) {
				System.err.println(help);
				retCode = 4;
				return;
            } else if (args[j].equalsIgnoreCase("-n") == true) {
                autoLogin = true;
	        } else if (args[j].equalsIgnoreCase("-script") == true) {
				if (++j < args.length) {
					script = new File(args[j]);
				} else {
					throw new FTPException("Too many args!");
				}				
	        } else if (args[j].equalsIgnoreCase("-passive") == true) {
	            passiveMode = true;
	        }
		}

		if (script == null) {
			is = System.in;
			FtpParser parser = new FtpParser(is);
			parser.setAutoLogin(autoLogin);
			parser.setPassive(passiveMode);
			while (true) {
				System.out.print("ftp>");
				try {
					parser.cmd_list();
				} catch (Exception e) {
					logger.error(e);
					System.out.println(e.getMessage());
					parser.ReInit(System.in);
				} catch (Throwable t) {
					logger.error(t);
					System.out.println(t.getMessage());
					parser.ReInit(System.in);
				}
			}
		} else {
			try {
				is = new FileInputStream(script);
			} catch (FileNotFoundException e) {
				throw new FTPException(e);
			}
			FtpParser parser = new FtpParser(is);
			parser.setAutoLogin(false);
			parser.setPassive(passiveMode);
			while (true) {
				try {
					parser.cmd_list();
				} catch (Exception e) {
					logger.error(e);
					System.out.println(e.getMessage());
				} catch (Throwable t) {
					logger.error(t);
					System.out.println(t.getMessage());
				}
			}
		}		
	}

	public static int getRetCode() {
		return retCode;
	}
	
	private static String help = " * Parameters:"
			+ "\n"
			+ "-port\n\tThe port to connect to on the remote host (default to 21)."
			+ "\n"
			+ "-p\n\tPassive mode (default false)."
			+ "\n"
			+ "-script file\n\tRun the script (default interactive/System.in)."
			+ "\n"
			+ "-n\n\tinhibit auto-login."
			+ "\n\n";

}
