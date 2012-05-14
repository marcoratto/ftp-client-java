package uk.co.marcoratto.ftp;

import java.io.File;

import uk.co.marcoratto.util.Utility;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestRunme extends TestBase {
	
	private final static String FTP_USER_WITHOUT_PASSWORD = "watir";
	private final static String FTP_WRONG_PASSWORD = "drowssaP";
	private final static String FTP_USER = "rattom";
	private final static String FTP_PASS = "rm";
	private final static String FTP_SERVER = "160.220.142.168";
	private final static String FTP_REMOTE_DIR = "/home/rattom/tmp";
			
	protected void setUp() {
		super.setUp();		
	}
	
	protected void tearDown() {
		System.out.println(this.getName() + ".tearDown()");
	}

	public static void main (String[] args) {
		TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(TestRunme.class);
	}	

	public void testMPUT() {
		System.out.println(this.getClass().getName() + ".testMPUT()");
		try {			 
			String filename = "testMPUT.txt";
			File file1 = new File("./test/samples/testMPUT", filename);			
			file1.delete();			
			Utility.stringToFile(file1, "UTF-8", filename);
			assertEquals(true, file1.exists());

			File script = new File("./test/samples/testMPUT", "testMPUT.ftp");
			StringBuffer sb = new StringBuffer("");
			sb.append("open " + FTP_SERVER);
			sb.append(Utility.NEWLINE);
			sb.append("user " + FTP_USER + " " + FTP_PASS);
			sb.append(Utility.NEWLINE);
			sb.append("cd " + FTP_REMOTE_DIR);
			sb.append(Utility.NEWLINE);
			sb.append("lcd " + file1.getParent());
			sb.append(Utility.NEWLINE);
			sb.append("prompt");
			sb.append(Utility.NEWLINE);
			sb.append("ascii");
			sb.append(Utility.NEWLINE);
			sb.append("mput " + filename);
			sb.append(Utility.NEWLINE);
			sb.append("bye");
			sb.append(Utility.NEWLINE);
			Utility.stringToFile(script, "UTF-8", sb.toString());
			assertEquals(true, script.exists());
			
			this.params.clear();
			this.params.add("-n");
			this.params.add("-script");
			this.params.add(script.getAbsolutePath());
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			int actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}
		
}
