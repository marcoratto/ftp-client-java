package uk.co.marcoratto.ftp;

import java.util.Vector;

import junit.framework.TestCase;

public abstract class TestBase extends TestCase {
	
	protected Runme runme = null;
	protected Vector<String> params = null;

	protected void setUp() {
		System.out.println(this.getName() + ".setUp()");	

		try {
			runme = new Runme();
			this.params = new Vector<String>();
			System.setProperty("ftp_config_file", "./test/res/junit.properties");			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 			
	}
	
}
