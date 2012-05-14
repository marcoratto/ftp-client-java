package uk.co.marcoratto.ftp;

import java.util.Vector;

import uk.co.marcoratto.ftp.Runme;

import junit.framework.TestCase;

public abstract class TestBase extends TestCase {
	
	protected Runme runme = null;
	protected Vector<String> params = null;

	// MUST be static for the Factory
	protected static Throwable actualThrowable = null;  
	protected static int actualEndTotalFiles;	
	protected static int actualStartTotalFiles;
	protected static int actualReturnCode;

	protected final static String USER_WITHOUT_PASSWORD = "watir";
	protected final static String WRONG_PASSWORD = "drowssaP";
	protected final static String USER = "rattom";
	protected final static String PASS = "rm";
	protected final static String SERVER = "160.220.142.168";
	protected final static String REMOTE_DIR = "/home/rattom/tmp";
	
	protected String remotePath = null;
	protected String remotePathWithoutPassword = null;

	protected void setUp() {
		System.out.println(this.getName() + ".setUp()");	

		try {
			runme = new Runme();
			this.params = new Vector<String>();
			System.setProperty("ftp_config_file", "./test/res/junit.properties");
			
			actualReturnCode = -1;
			actualEndTotalFiles = -1;
			actualStartTotalFiles = -1;
			actualThrowable = null;

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 			
	}
	
}
