package uk.co.marcoratto.ftp;

import uk.co.marcoratto.ftp.FTP;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestFTP extends TestBase {
		
	private String remotePath = null;
	
	private FTP ftp = null;
				
	protected void setUp() {
		super.setUp();
		this.ftp = new FTP(null);
		this.remotePath = USER + ":" + PASS + "@" + SERVER + ":" + REMOTE_DIR;
	}
	
	protected void tearDown() {
		System.out.println(this.getName() + ".tearDown()");
	}

	public static void main (String[] args) {
		TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(TestFTP.class);
	}	
			
	public void testBinaryTransfer() {
		System.out.println(this.getClass().getName() + ".testBinaryTransfer()");
		try {			 					
			boolean expected = true;
			this.ftp.setBinaryTransfer(expected);
			boolean actual = this.ftp.isBinaryTransfer();
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}
		
	public void testControlKeepAliveReplyTimeout() {
		System.out.println(this.getClass().getName() + ".testControlKeepAliveReplyTimeout()");
		try {			 					
			int expected = 123;
			this.ftp.setControlKeepAliveReplyTimeout(expected);
			int actual = this.ftp.getControlKeepAliveReplyTimeout();
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testPort() {
		System.out.println(this.getClass().getName() + ".testPort()");
		try {			 					
			int expected = 21;
			this.ftp.setPort(expected);
			int actual = this.ftp.getPort();
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testKeepAliveTimeout() {
		System.out.println(this.getClass().getName() + ".testKeepAliveTimeout()");
		try {			 					
			long expected = 12345;
			this.ftp.setKeepAliveTimeout(expected);
			long actual = this.ftp.getKeepAliveTimeout();
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testRecursive() {
		System.out.println(this.getClass().getName() + ".testRecursive()");
		try {			 					
			boolean expected = true;
			this.ftp.setRecursive(expected);
			
			boolean actual = this.ftp.isRecursive();
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}
}
