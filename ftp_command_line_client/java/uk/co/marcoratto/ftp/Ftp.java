package uk.co.marcoratto.ftp;

import org.apache.log4j.Logger;

public class Ftp {

	private final static Logger logger = Logger.getLogger("uk.co.marcoratto.ftp");

	public static void main(String args[]) throws ParseException {
		logger.debug("main");
		boolean autoLogin = true;
		boolean passiveMode = false;
		
		for (int j = 0; j < args.length; j++) {
            // logger.debug(args[j]);
			logger.debug("args[" + j + "]:" + args[j]);
			if (args[j].equalsIgnoreCase("-h") == true) {
				System.err.println(usage);
				System.exit(1);
            } else if (args[j].equalsIgnoreCase("-n") == true) {
                if (++j < args.length) {
                	autoLogin = args[j].trim().equalsIgnoreCase("true");
                }
	        } else if (args[j].equalsIgnoreCase("-p") == true) {
	            if (++j < args.length) {
	            	passiveMode = args[j].trim().equalsIgnoreCase("true");
	            }
        }
		}
		logger.debug("autoLogin=" + autoLogin);
		logger.debug("passiveMode=" + passiveMode);
		
		FtpParser parser = new FtpParser(System.in);
		parser.setAutoLogin(autoLogin);
		parser.setPassive(passiveMode);
		while (true) {
			System.out.print("ftp>");
			try {
				parser.cmd_list();
			} catch (Exception e) {
				logger.error(e);
				System.out.println("?Ambiguous command");
				parser.ReInit(System.in);
			} catch (Throwable t) {
				logger.error(t);
				System.out.println("?Ambiguous command");
				parser.ReInit(System.in);
			}
		}
		
	}
	
	final static String usage = "Usage: [-nhp] [hostname]\n\n" +
	   							 "\t-h: show this help\n" +
	   							 "\t-p: enable passive mode\n" +
	   							 "\t-n: inhibit auto-login\n";

}
