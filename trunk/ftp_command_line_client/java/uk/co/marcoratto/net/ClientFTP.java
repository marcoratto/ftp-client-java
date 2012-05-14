/*
 * Copyright (C) 2011 Marco Ratto
 *
 * This file is part of the project ftp-client-java.
 *
 * ftp-client-java is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * any later version.
 *
 * ftp-client-java is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ftp-client-java; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package uk.co.marcoratto.net;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.log4j.Logger;

public class ClientFTP implements FTPFileFilter {
	
	private final static Logger logger = Logger.getLogger("uk.co.marcoratto.ftp");
	private final static int DEFAULT_BUFFER_SIZE = 1024;
	
	private FTPClient ftp = null;
	
	public ClientFTP() {
		logger.debug("ClientFTP.ClientFTP()");
		ftp = new FTPClient();
	}
	
	/** A convenience method for connecting and logging in */
	public int connect(String host, int port) throws ClientFTPException {
		int reply = -1;
		try {
			ftp.connect(host, port);
			reply = ftp.getReplyCode();
			ftp.addProtocolCommandListener(new PrintCommandListener());			
		} catch (SocketException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		}
		return reply;
	}
	
	public int login(String username, String password, String account) throws ClientFTPException {
		int reply = -1;
		try {									
			if (account == null) {
				ftp.login(username, password);				
			} else {
				ftp.login(username, password, account);				
			}
			reply = ftp.getReplyCode();		
			this.printReplyStrings();			
			System.out.println("Remote system is " + ftp.getSystemType());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		}
		return reply;
	}
	
	public void printReplyStrings() {
		String output[] = ftp.getReplyStrings();
		for (int j=0; j<output.length; j++) {
		  System.out.println(output[j]);
		}				
	}
	
	public int disconnect() throws ClientFTPException {
		int reply = -1;
		if (ftp != null) {
			try {
				ftp.logout();
				reply = ftp.getReplyCode();
				this.printReplyStrings();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new ClientFTPException(e);
			}
		}
		return reply;
	}
	
	/** Turn passive transfer mode on or off. If Passive mode is active, a
	  * PASV command to be issued and interpreted before data transfers;
	  * otherwise, a PORT command will be used for data transfers. If you're
	  * unsure which one to use, you probably want Passive mode to be on. */
	public void setPassiveMode(boolean setPassive) {
		if (setPassive) {
			ftp.enterLocalPassiveMode();
		} else {
			ftp.enterLocalActiveMode();
		}
	}
	
	/** Use ASCII mode for file transfers */
	public boolean setFileTypeASCII() throws ClientFTPException {
		boolean reply = false;
		try {
			reply = ftp.setFileType(FTP.ASCII_FILE_TYPE);
			this.printReplyStrings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		}
		return reply;
	}
	
	/** Use Binary mode for file transfers */
	public boolean setFileTypeBINARY() throws ClientFTPException {
		boolean reply = false;
		try {
			reply =  ftp.setFileType(FTP.BINARY_FILE_TYPE);
			this.printReplyStrings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		}
		return reply;
	}
		
	public int pwd() throws ClientFTPException {
		int reply = 0;
		try {
			reply = ftp.pwd();
			this.printReplyStrings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		}
		return reply;
	}
	
	public File getRemoteDirectory() throws ClientFTPException {
		File out = null;
		try {
			out = new File(ftp.printWorkingDirectory());
		} catch (IOException e) {
			out = null;
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		}		
		return out;
	}
	
	public int cwd(String directory) throws ClientFTPException {
		int reply = 0;
		try {
			reply = ftp.cwd(directory);
			this.printReplyStrings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);			
		}
		return reply;
	}
	
	public int mkdir(String pathname) throws ClientFTPException {
		int reply = 0;
		try {
			reply = ftp.mkd(pathname);
			this.printReplyStrings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		}
		return reply;
	}

	public boolean rmdir(String pathname) throws ClientFTPException {
		boolean reply = false;
		try {
			reply = ftp.removeDirectory(pathname);				
			this.printReplyStrings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		}
		return reply;
	}

	public boolean ls(String pathname, String filter) throws ClientFTPException {
		boolean reply = false;
		try {
		 	FileFilter fileFilter = new WildcardFileFilter(filter);
		 	
			FTPFile[] files = null;
			if (pathname == null) {
				files = ftp.listFiles(".", this);				
			} else {				
				files = ftp.listFiles(pathname, this);				
			}
			this.printReplyStrings();
			for (int i = 0; i < files.length; i++) {				
				System.out.println(files[i]);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);			
		}
		return reply;
	}

	public boolean ls(String pathname) throws ClientFTPException {
		boolean reply = false;
		try {
			FTPFile[] files = null;
			if (pathname == null) {
				files = ftp.listFiles();				
			} else {
				files = ftp.listFiles(pathname);				
			}
			this.printReplyStrings();
			for (int i = 0; i < files.length; i++) {				
				System.out.println(files[i]);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);			
		}
		return reply;
	}

	public int rhelp() throws ClientFTPException {
		int reply = -1;
		try {
			reply = ftp.help();		
			this.printReplyStrings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		}
		return reply;
	}

	public boolean upload(String localFile, String remoteFile) throws ClientFTPException {
		boolean reply = false;
		FileInputStream in = null;
		try {
			in = new FileInputStream(localFile);
						
			ftp.setBufferSize(DEFAULT_BUFFER_SIZE);
			reply = ftp.storeFile(remoteFile, in);
			this.printReplyStrings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}							
			}
		}
		
		return reply;
	}

	public int system() throws ClientFTPException {
		int reply = 0;
		try {
			reply = ftp.syst();
			this.printReplyStrings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		}
		return reply;
	}

	public boolean rename(String oldName, String newName) throws ClientFTPException {
		boolean reply = false;
		try {
			reply = ftp.rename(oldName, newName);
			this.printReplyStrings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		} 
		
		return reply;
	}
	
	public boolean deleteFile(String pathname) throws ClientFTPException {
		boolean reply = false;
		try {
			reply = ftp.deleteFile(pathname);			
			this.printReplyStrings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		}
		return reply;
	}
		
	public boolean downloadFile(String serverFile, String localFile) throws ClientFTPException {
		FileOutputStream out = null;
		boolean result = false;
		try {
			logger.info("localFile=" + localFile);
			out = new FileOutputStream(localFile);
			logger.info("serverFile=" + serverFile);
			result = ftp.retrieveFile(serverFile, out);			
			logger.info("result=" + result);
			this.printReplyStrings();
			out.flush();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}				
			}
		}
		return result;
	}
	
	public long size(String filename) throws ClientFTPException {
		FTPFile[] files = null;
		long out = -1;
		try {
			files = ftp.listFiles(filename);
			if (files.length != 1) {
				out = -1;
			} else {
				out = files[0].getSize();				
			}
		} catch (IOException e) {
			logger.warn(e.getMessage(), e);
			throw new ClientFTPException(e);
		}			
		return out;
	}
	
	public int chmod(int mode, String filename) throws ClientFTPException {
		return this.sendCommand("CHMOD ", mode + " " + filename);
	}
	
	public int sendCommand(String command, String args) throws ClientFTPException {
		int reply = -1;
		try {
			logger.info(command + args);
			reply = ftp.sendCommand(command, args);			
			this.printReplyStrings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);
		} 		
		return reply;
	}

	public boolean nlist(String pathname) throws ClientFTPException {
		boolean reply = false;
		try {
			FTPFile[] files = null;
			if (pathname == null) {
				files = ftp.listFiles();				
			} else {
				files = ftp.listFiles(pathname);	
			}
			this.printReplyStrings();
			for (int i = 0; i < files.length; i++) {				
				System.out.println(files[i].getName());				
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);			
		}
		return reply;
	}
	
	public FTPFile[] getRemoteFiles(String pathname) throws ClientFTPException {
		FTPFile[] files = null;
		try {
			if (pathname == null) {
				files = ftp.listFiles();				
			} else {
				files = ftp.listFiles(pathname);	
			}
			this.printReplyStrings();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new ClientFTPException(e);			
		}
		return files;
	}

	protected void finalize() {
		    try {
		    	if (this.ftp != null) {
			        this.disconnect();		    		
		    	}
		    } catch (ClientFTPException e) {
				logger.error(e.getMessage(), e);
		    }
	  }

	@Override
	public boolean accept(FTPFile arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}

