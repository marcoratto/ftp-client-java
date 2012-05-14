package uk.co.marcoratto.ftp;

import java.io.File;

import uk.co.marcoratto.ftp.Runme;
import uk.co.marcoratto.ftp.listeners.Listener;
import uk.co.marcoratto.ftp.listeners.ListenerException;
import uk.co.marcoratto.util.Utility;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestRunme extends TestBase implements Listener {
				
	protected void setUp() {
		super.setUp();		
		remotePath = USER + ":" + PASS + "@" + SERVER + ":" + REMOTE_DIR;
		remotePathWithoutPassword = USER + "@" + SERVER + ":" + REMOTE_DIR;
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

	public void testParamAskPassword() {
		System.out.println(this.getClass().getName() + ".testParamAskPassword()");
		try {			 
			String filename = "testParamAskPassword.txt";
			File file1 = new File("./test/samples/testParamAskPassword", filename);			
			file1.delete();			
			Utility.stringToFile(file1, "UTF-8", "testParamAskPassword.txt");
			assertEquals(true, file1.exists());

			this.params.clear();
			this.params.add("-source");
			this.params.add(file1.getAbsolutePath());
			this.params.add("-target");
			this.params.add(this.remotePathWithoutPassword + "/" + filename); 
			this.params.add("-o");
			this.params.add("-ask");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(1, actualEndTotalFiles);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testUploadOneFileWithOverwrite() {
		System.out.println(this.getClass().getName() + ".testUploadOneFileWithOverwrite()");
		try {			 
			String filename = "testUploadOneFileWithOverwrite.txt";
			File file1 = new File("./test/samples/testUploadOneFileWithDelete", filename);			
			file1.delete();			
			Utility.stringToFile(file1, "UTF-8", "testUploadOneFileWithDelete.txt");
			assertEquals(true, file1.exists());

			this.params.clear();
			this.params.add("-source");
			this.params.add(file1.getAbsolutePath());
			this.params.add("-target");
			this.params.add(this.remotePath + "/" + filename); 
			this.params.add("-o");
			this.params.add("-v");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(1, actualEndTotalFiles);

			this.params.clear();
			this.params.add("-source");
			this.params.add(file1.getAbsolutePath());
			this.params.add("-target");
			this.params.add(this.remotePath + "/" + filename); 
			this.params.add("-v");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			String actual = actualThrowable.getMessage();
			String expected = "uk.co.marcoratto.ftp.FTPException: Remote file " + REMOTE_DIR + "/" + filename + " already exists!";
			System.out.println("actual is " + actual);
			System.out.println("expected is " + expected);
			assertEquals(expected, actual);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(2, actualReturnCode);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testUnknownParam() {
		System.out.println(this.getClass().getName() + ".testUnknownParam()");
		try {			 
			this.params.clear();
			this.params.add("-testUnknownParam");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));			
			
			String actual = actualThrowable.getMessage();
			String expected = "Parameter '-testUnknownParam' unknown!";
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);

			actualReturnCode = Runme.getRetCode();
			assertEquals(2, actualReturnCode);
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testHelp() {
		System.out.println(this.getClass().getName() + ".testHelp()");
		try {			 
			this.params.clear();
			this.params.add("-h");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));			
			assertNull(actualThrowable);
			actualReturnCode = Runme.getRetCode();
			assertEquals(4, actualReturnCode);
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testParamPort() {
		System.out.println(this.getClass().getName() + ".testParamPort()");
		try {			 
			this.params.clear();
			this.params.add("-source");
			this.params.add("./test/samples/testUploadOneFile/dummy.txt");
			this.params.add("-target");
			this.params.add(this.remotePath + "/dummy.txt"); 
			this.params.add("-o");
			this.params.add("-port");
			this.params.add("21");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(1, actualEndTotalFiles);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}
	
	public void testRemotetoRemoteException() {
		System.out.println(this.getClass().getName() + ".testRemotetoRemoteException()");
		try {			 
			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remotePath + "/dummy1.txt"); 
			this.params.add("-target");
			this.params.add(this.remotePath + "/dummy.txt"); 
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			String actual = actualThrowable.getMessage();
			String expected = "uk.co.marcoratto.ftp.FTPException: Copying from a remote server to a remote server is not supported.";
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(2, actualReturnCode);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testParamSourceNull() {
		System.out.println(this.getClass().getName() + ".testParamSourceNull()");
		try {			 
			this.params.clear();
			this.params.add("-target");
			this.params.add(this.remotePath + "/dummy.txt"); 
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));

			String actual = actualThrowable.getMessage();
			String expected = "uk.co.marcoratto.ftp.FTPException: Why the parameter 'fromUri' is null ?";
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(2, actualReturnCode);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}
	
	public void testParamTargetNull() {
		System.out.println(this.getClass().getName() + ".testParamTargetNull()");
		try {			 
			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remotePath + "/dummy.txt"); 
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));

			String actual = actualThrowable.getMessage();
			String expected = "uk.co.marcoratto.ftp.FTPException: Why the parameter 'toUri' is null ?";
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);

			actualReturnCode = Runme.getRetCode();			
			assertEquals(2, actualReturnCode);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	/*
	 * openssl passwd -1 -salt ADUODeAy
	 */
	public void testUploadWithoutPassword() {
		System.out.println(this.getClass().getName() + ".testUploadWithoutPassword()");
			try {			 
				remotePath = USER_WITHOUT_PASSWORD + ":@" + SERVER + ":" + REMOTE_DIR;
				this.params.clear();
				this.params.add("-source");
				this.params.add("./test/samples/testUploadWithoutPassword/testUploadWithoutPassword.txt");
				this.params.add("-target");
				this.params.add(this.remotePath + "/testUploadWithoutPassword.txt"); 
				this.params.add("-o");
				runme = new Runme();
				runme.runme((String[]) this.params.toArray(new String[0]));
				
				assertNull(actualThrowable);
				
				actualReturnCode = Runme.getRetCode();			
				assertEquals(0, actualReturnCode);
				
				assertEquals(1, actualEndTotalFiles);

			} catch (Throwable t) {
				t.printStackTrace();
				fail(t.getMessage());
			} 					
	}

	public void testUploadWithoutUsernameAndPassword() {
		System.out.println(this.getClass().getName() + ".testUploadWithoutUsernameAndPassword()");
			try {			 
				remotePath = "@192.168.1.3:/home/rattom/tmp";
				this.params.clear();
				this.params.add("-source");
				this.params.add("./test/samples/testUploadWithoutUsernameAndPassword/testUploadWithoutUsernameAndPassword.txt");
				this.params.add("-target");
				this.params.add(this.remotePath + "/testUploadWithoutUsernameAndPassword.txt"); 
				runme = new Runme();
				runme.runme((String[]) this.params.toArray(new String[0]));

				String actual = actualThrowable.getMessage();
				String expected = "uk.co.marcoratto.ftp.FTPException: java.net.ConnectException: Connection refused";
				System.out.println(actual);
				System.out.println(expected);
				assertEquals(expected, actual);

				actualReturnCode = Runme.getRetCode();			
				assertEquals(2, actualReturnCode);
				
			} catch (Throwable t) {
				t.printStackTrace();
				fail(t.getMessage());
			} 					
	}

	public void testUploadWithWrongPassword() {
		System.out.println(this.getClass().getName() + ".testUploadWithWrongPassword()");
			try {			 
				remotePath = USER + ":" + WRONG_PASSWORD + "@" + SERVER + ":" + REMOTE_DIR;
				this.params.clear();
				this.params.add("-source");
				this.params.add("./test/samples/testUploadWithWrongPassword/testUploadWithWrongPassword.txt");
				this.params.add("-target");
				this.params.add(this.remotePath + "/testUploadWithWrongPassword.txt"); 
				runme = new Runme();
				runme.runme((String[]) this.params.toArray(new String[0]));
				
				String actual = actualThrowable.getMessage();
				String expected = "uk.co.marcoratto.ftp.FTPException: Error on login with username 'rattom' and password 'drowssaP'";
				System.out.println(actual);
				System.out.println(expected);
				assertEquals(expected, actual);

				actualReturnCode = Runme.getRetCode();			
				assertEquals(2, actualReturnCode);
				
			} catch (Throwable t) {
				t.printStackTrace();
				fail(t.getMessage());
			} 					
	}

	public void testUploadOneFileWithDelete() {
		System.out.println(this.getClass().getName() + ".testUploadOneFileWithDelete()");
		try {			 
			File file1 = new File("./test/samples/testUploadOneFileWithDelete/testUploadOneFileWithDelete.txt");			
			file1.delete();			
			Utility.stringToFile(file1, "UTF-8", "testUploadOneFileWithDelete.txt");

			assertEquals(true, file1.exists());
			
			this.params.clear();
			this.params.add("-source");
			this.params.add("./test/samples/testUploadOneFileWithDelete/testUploadOneFileWithDelete.txt");
			this.params.add("-target");
			this.params.add(this.remotePath + "/testUploadOneFileWithDelete.txt"); 
			this.params.add("-d");
			this.params.add("-o");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(1, actualEndTotalFiles);

			assertEquals(false, file1.exists());
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testUploadOneFileNotExists() {
		System.out.println(this.getClass().getName() + ".testUploadOneFileNotExists()");
		try {			 
			File file1 = new File("./test/samples/testUploadOneFileNotExists", "testUploadOneFileNotExists.txt");
			
			this.params.clear();
			this.params.add("-source");
			this.params.add(file1.getCanonicalPath());
			this.params.add("-target");
			this.params.add(this.remotePath + "/testUploadOneFileNotExists.txt"); 
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			String actual = actualThrowable.getMessage();
			String expected = "uk.co.marcoratto.ftp.FTPException: " + file1.getCanonicalPath() + " not valid!";
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(2, actualReturnCode);			

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testUploadOneFile() {
		System.out.println(this.getClass().getName() + ".testUploadOneFile()");
		try {			 
			this.params.clear();
			this.params.add("-source");
			this.params.add("./test/samples/testUploadOneFile/dummy.txt");
			this.params.add("-target");
			this.params.add(this.remotePath + "/dummy.txt"); 
			this.params.add("-o");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(1, actualEndTotalFiles);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}
	
	public void testUploadEmptyFile() {
		System.out.println(this.getClass().getName() + ".testUploadEmptyFile()");
		try {			 
			File localFile = new File("./test/samples/testUploadEmptyFile/testUploadEmptyFile.txt");
			this.params.add("-source");
			this.params.add(localFile.getCanonicalPath());
			this.params.add("-target");
			this.params.add(this.remotePath + "/testUploadEmptyFile.txt"); 
			runme.runme((String[]) this.params.toArray(new String[0]));

			String actual = actualThrowable.getMessage();
			String expected = "uk.co.marcoratto.ftp.FTPException: Local file is empty!";
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(2, actualReturnCode);			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testDownloadFiles() {
		System.out.println(this.getClass().getName() + ".testDownloadFiles()");
		try {			 
			Utility.stringToFile(new File("./test/samples/tmp/testDownloadFiles-01.txt"), "UTF-8", "testDownloadFiles-01.txt");
			Utility.stringToFile(new File("./test/samples/tmp/testDownloadFiles-02.txt"), "UTF-8", "testDownloadFiles-02.txt");
			Utility.stringToFile(new File("./test/samples/tmp/testDownloadFiles-03.txt"), "UTF-8", "testDownloadFiles-03.txt");

			this.params.clear();
			this.params.add("-source");
			this.params.add("./test/samples/tmp/testDownloadFiles-*.txt"); 
			this.params.add("-target");
			this.params.add(this.remotePath);
			this.params.add("-o");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));

			assertNull(actualThrowable);
			
			File localDir = new File("./test/samples/testDownloadFiles");
			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remotePath + "/testDownloadFiles-*.txt"); 
			this.params.add("-target");
			this.params.add(localDir.getCanonicalPath());
			this.params.add("-o");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(3, actualEndTotalFiles);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testDownloadOneFileWithOverwrite() {
		System.out.println(this.getClass().getName() + ".testDownloadOneFileWithOverwrite()");
		try {			 
			String filename = "testDownloadOneFileWithOverwrite.txt";
			File file1 = new File("./test/samples/tmp", filename);
			Utility.stringToFile(file1, "UTF-8", filename);
			assertEquals(true, file1.exists());

			this.params.clear();
			this.params.add("-source");
			this.params.add(file1.getCanonicalPath()); 
			this.params.add("-target");
			this.params.add(this.remotePath + "/" + filename);
			this.params.add("-o");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));

			assertNull(actualThrowable);
			
			file1.delete();
			
			File file2 = new File("./test/samples/testDownloadOneFileWithOverwrite", filename);
			Utility.stringToFile(file2, "UTF-8", filename);
			assertEquals(true, file2.exists());
			
			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remotePath + "/" + filename); 
			this.params.add("-target");
			this.params.add(file2.getAbsolutePath());
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			String actual = actualThrowable.getMessage();
			String expected = "uk.co.marcoratto.ftp.FTPException: Local file " + file2.getAbsolutePath() + " already exists!";
			System.out.println(" actual :" + actual);
			System.out.println("expected:" + expected);
			assertEquals(expected, actual);

			actualReturnCode = Runme.getRetCode();			
			assertEquals(2, actualReturnCode);			
			assertEquals(1, actualEndTotalFiles);
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testDownloadOneFileWithDelete() {
		System.out.println(this.getClass().getName() + ".testDownloadOneFileWithDelete()");
		try {			 
			String filename = "testDownloadOneFileWithDelete.txt";
			File file1 = new File("./test/samples/tmp", filename);
			Utility.stringToFile(file1, "UTF-8", filename);

			this.params.clear();
			this.params.add("-source");
			this.params.add(file1.getCanonicalPath()); 
			this.params.add("-target");
			this.params.add(this.remotePath + "/" + filename);
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));

			assertNull(actualThrowable);
			
			file1.delete();
			
			File file2 = new File("./test/samples/testDownloadOneFileWithNameWithDelete", filename);
			if (file2.exists()) {
				file2.delete();				
			}
			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remotePath + "/" + filename); 
			this.params.add("-target");
			this.params.add(file2.getCanonicalPath());
			this.params.add("-d");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(1, actualEndTotalFiles);
			assertEquals(true, file2.exists());

			file2.delete();				
			actualEndTotalFiles = -1;
			
			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remotePath + "/" + filename); 
			this.params.add("-target");
			this.params.add(file2.getCanonicalPath());
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
						
			String actual = actualThrowable.getMessage();
			String expected = "uk.co.marcoratto.ftp.FTPException: Remote file /home/rattom/tmp/" + filename + " not found!";
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(2, actualReturnCode);
			
			assertEquals(-1, actualEndTotalFiles);

			assertEquals(false, file2.exists());
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testDownloadOneFileNotExists() {
		System.out.println(this.getClass().getName() + ".testDownloadOneFileNotExists()");
		try {			 
			File localFile = new File("./test/samples/testDownloadOneFileNotExists/testDownloadOneFileNotExists.txt");
			if (localFile.exists()) {
				localFile.delete();				
			}
			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remotePath + "/testDownloadOneFileNotExists.txt"); 
			this.params.add("-target");
			this.params.add(localFile.getCanonicalPath());
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
						
			String actual = actualThrowable.getMessage();
			String expected = "uk.co.marcoratto.ftp.FTPException: Remote file /home/rattom/tmp/testDownloadOneFileNotExists.txt not found!";
			System.out.println(actual);
			System.out.println(expected);
			assertEquals(expected, actual);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(2, actualReturnCode);
			
			assertEquals(-1, actualEndTotalFiles);

			assertEquals(false, localFile.exists());
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testDownloadOneFileWithName() {
		System.out.println(this.getClass().getName() + ".testDownloadOneFileWithName()");
		try {			 
			File localFile = new File("./test/samples/testDownloadOneFileWithName/dummy.txt");
			if (localFile.exists()) {
				localFile.delete();				
			}
			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remotePath + "/dummy.txt"); 
			this.params.add("-target");
			this.params.add(localFile.getCanonicalPath());
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(1, actualEndTotalFiles);

			assertEquals(true, localFile.exists());
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}
	
	public void testDownloadOneFileWithoutTargetFilename() {
		System.out.println(this.getClass().getName() + ".testDownloadOneFileWithoutTargetFilename()");
		try {			 
			File localDir = new File("./test/samples/testDownloadOneFileWithoutTargetFilename");
			File localFile = new File(localDir, "dummy.txt");
			if (localFile.exists()) {
				localFile.delete();				
			}
			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remotePath + "/dummy.txt"); 
			this.params.add("-target");
			this.params.add(localDir.getCanonicalPath());
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(1, actualEndTotalFiles);

			assertEquals(true, localFile.exists());
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}

	public void testUploadDirectory() {
		System.out.println(this.getClass().getName() + ".testUploadDirectory()");
		try {			 
			File file1 = new File("./test/samples/testUploadDirectory/testUploadDirectory-01.txt");
			File file2 = new File("./test/samples/testUploadDirectory/testUploadDirectory-02.txt");
			File file3 = new File("./test/samples/testUploadDirectory/testUploadDirectory-03.txt");
			
			file1.delete();
			file2.delete();
			file3.delete();
			
			Utility.stringToFile(file1, "UTF-8", "testUploadDirectory-01.txt");
			Utility.stringToFile(file2, "UTF-8", "testUploadDirectory-02.txt");
			Utility.stringToFile(file3, "UTF-8", "testUploadDirectory-02.txt");
			
			assertEquals(true, file1.exists());
			assertEquals(true, file2.exists());
			assertEquals(true, file3.exists());
			
			this.params.clear();
			this.params.add("-source");
			this.params.add("./test/samples/testUploadDirectory");
			this.params.add("-target");
			this.params.add(this.remotePath); 
			this.params.add("-o");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(3, actualEndTotalFiles);

			file1.delete();
			file2.delete();
			file3.delete();

			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remotePath + "/testUploadDirectory-*.txt"); 
			this.params.add("-target");
			this.params.add("./test/samples/testUploadDirectory");
			this.params.add("-o");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(3, actualEndTotalFiles);

			assertEquals(true, file1.exists());
			assertEquals(true, file2.exists());
			assertEquals(true, file3.exists());

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}	

	public void testSendMultipleFiles() {
		System.out.println(this.getClass().getName() + ".testSendMultipleFiles()");
		try {			 
			File file1 = new File("./test/samples/testSendMultipleFiles/testSendMultipleFiles-01.txt");
			File file2 = new File("./test/samples/testSendMultipleFiles/testSendMultipleFiles-02.txt");
			File file3 = new File("./test/samples/testSendMultipleFiles/testSendMultipleFiles-03.txt");
			
			file1.delete();
			file2.delete();
			file3.delete();
			
			Utility.stringToFile(file1, "UTF-8", "testSendMultipleFiles-01.txt");
			Utility.stringToFile(file2, "UTF-8", "testSendMultipleFiles-02.txt");
			Utility.stringToFile(file3, "UTF-8", "testSendMultipleFiles-02.txt");
			
			assertEquals(true, file1.exists());
			assertEquals(true, file2.exists());
			assertEquals(true, file3.exists());

			this.params.clear();
			this.params.add("-source");
			this.params.add("./test/samples/testSendMultipleFiles/*.txt");
			this.params.add("-target");
			this.params.add(this.remotePath); 
			this.params.add("-v");
			this.params.add("-o");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(3, actualEndTotalFiles);

			file1.delete();
			file2.delete();
			file3.delete();

			this.params.clear();
			this.params.add("-source");
			this.params.add(this.remotePath + "/testSendMultipleFiles*.txt"); 
			this.params.add("-target");
			this.params.add("./test/samples/testSendMultipleFiles");
			this.params.add("-v");
			this.params.add("-o");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(3, actualEndTotalFiles);

			assertEquals(true, file1.exists());
			assertEquals(true, file2.exists());
			assertEquals(true, file3.exists());
		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}	
	
	public void testSendMultipleFilesWithSubdirectories() {
		System.out.println(this.getClass().getName() + ".testSendMultipleFilesWithSubdirectories()");
		try {			 
			this.params.clear();
			this.params.add("-source");
			this.params.add("./test/samples/testSendMultipleFilesWithSubdirectories/*.txt");
			this.params.add("-target");
			this.params.add(this.remotePath); 
			this.params.add("-r");
			this.params.add("-o");
			runme = new Runme();
			runme.runme((String[]) this.params.toArray(new String[0]));
			
			assertNull(actualThrowable);
			
			actualReturnCode = Runme.getRetCode();			
			assertEquals(0, actualReturnCode);
			
			assertEquals(4, actualEndTotalFiles);

		} catch (Throwable t) {
			t.printStackTrace();
			fail(t.getMessage());
		} 		
	}		

	public void onStartUpload(int counter, int totalFiles, File fromFile, String toUri) throws ListenerException {
		System.out.println("TestRunme.onStartUpload(): File " + counter + "/" + totalFiles + "=" + fromFile);
		}

		public void onEndUpload(int counter, int totalFiles, File fromFile, String toUri) throws ListenerException {
			System.out.println("TestRunme.onEndUpload(): File" + counter + "/" + totalFiles + "=" + fromFile);	
			actualEndTotalFiles = totalFiles;
		}

		public void onStartDownload(int counter, int totalFiles, String fromUri, String toUri)
				throws ListenerException {
		}

		public void onEndDownload(int counter, int totalFiles, String fromUri, String toUri) throws ListenerException {
			System.out.println("TestRunme:" + counter + "/" + totalFiles + ":" + fromUri);	
			actualEndTotalFiles = totalFiles;
		}

		public void onError(Throwable t) throws ListenerException {
			System.out.println("TestRunme: Method 'onError' invoked.");
			actualThrowable = t;
			actualThrowable.printStackTrace(System.err);
		}

		public void onStart() throws ListenerException {
			System.out.println("TestRunme: Method 'onStart' invoked.");
		}

		public void onEnd(int rc) throws ListenerException {
			System.out.println("TestRunme: Method 'onEnd' invoked. Return code is " + rc);
			actualReturnCode = rc;
		}
		
}
