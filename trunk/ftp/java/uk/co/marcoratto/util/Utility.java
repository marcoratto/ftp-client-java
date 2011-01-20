package uk.co.marcoratto.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.log4j.Logger;

public final class Utility {
	
	private final static Logger logger = Logger.getLogger("uk.co.marcoratto.ftp");
	
	private final static String NEWLINE = System.getProperty("line.separator");

	public static long getFileSize(String file) {
		long out = -1;
		try {
			File f = new File(file);			
			out = f.length();
		} catch (Exception e) {
			out = -1;
			logger.error(e.getMessage(), e);
		}				
		
		return out;		
	}
	
	public static String concatenate(File directory, String file) {
		if ((directory == null) || (file == null)) {
			return null;
		}
		String out = null;
		try {
			File f = new File(directory, file);
			out = f.getCanonicalPath();
		} catch (Exception e) {
			out = null;
			logger.error(e.getMessage(), e);
		}				
		
		return out;				
	}
	
	public static String msToSecondss(long ms) {
		int seconds = (int) ((ms / 1000) % 60);
		int minutes = (int) ((ms / 1000) / 60);		
		return minutes + "." + seconds;
	}
	
	public static boolean checkFile(String file) {
		boolean out = false;
		try {
			File f = new File(file);
			out = f.exists();
		} catch (Exception e) {
			out = false;
			logger.error(e.getMessage(), e);
		}
		
		return out;
	}
	
	public static boolean checkHostname(String host) {
		boolean out = false;
		try {
		    InetAddress.getByName(host);		   
		    out = true;		    
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
			out = false;
		}
		return out;
	}
	
	public static String input(String msg, String defaultValue) {
		String out = defaultValue;
		  System.out.print(msg);
		  InputStreamReader input = new InputStreamReader(System.in);
		  BufferedReader reader = new BufferedReader(input);
			try {
	    		String s = reader.readLine();
	    		if ((s == null) || (s.trim().length() == 0)) {
	    			out = defaultValue;
	    		} else {
	    			out = s;
	    		}
			} catch(Exception e) {
				out = defaultValue;
				logger.error(e.getMessage(), e);
			} 
			return out;
	}
	
	public static void exec(String cmd) {
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void execShell() {
		try {
			Runtime.getRuntime().exec("/bin/bash");
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static String translateBooleanToOnOff(boolean b) {
		if (b) {
			return "on";
		} else {
			return "off";
		}
	}
	
	public static String translateIntegerValueToOnOff(int i) {
		if (i>0) {
			return "on";
		} else {
			return "off";
		}
	}

	public static String translateBinary(boolean b) {
		if (b) {
			return "BINARY";
		} else {
			return "ASCII";
		}
	}

	public static int stringToIntegerValue(String s, int defaultValue) {
		int out = defaultValue;
		try {
			out = Integer.parseInt(s, 10);			
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(), e);
		}
			
		return out;
	}

	public static String getDefaultUser() {
		return System.getProperty("user.name");
	}
	
    public String fileToString(File file, String encoding) throws UtilityException {
        BufferedReader br = null;
        StringBuffer out = new StringBuffer("");
        try {
                logger.debug("Try to read file " + file.getAbsolutePath().toString() + ".");
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
                String line = null;
                String space = "";
                while ((line = br.readLine()) != null) {
                        out.append(space);
                        out.append(line);
                        space = NEWLINE;
                }
                logger.info("Read " + out.length() + " bytes from file.");
                logger.info("The buffer is:" + NEWLINE + out.toString());
        } catch (Throwable t) {
                logger.error(t.getMessage(), t);
                throw new UtilityException(t);
        } finally {
                if (br != null) {
                        try {
                                        br.close();
                        } catch (Exception e) {
                			logger.warn(e.getMessage(), e);
                        }
                }
        }
        return out.toString();
}
    
    public static String formatDouble (String pattern, double value) {
    	Locale currentLocale = Locale.getDefault();
    	NumberFormat nf = NumberFormat.getNumberInstance(currentLocale);
    	DecimalFormat df = (DecimalFormat)nf;
    	df.applyPattern(pattern);
    	return df.format(value);    	
    }

}
