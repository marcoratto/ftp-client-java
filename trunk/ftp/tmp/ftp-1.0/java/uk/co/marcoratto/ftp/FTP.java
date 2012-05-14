package uk.co.marcoratto.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.util.TrustManagerUtils;
import org.apache.log4j.Logger;

import uk.co.marcoratto.file.maketree.MakeTree;
import uk.co.marcoratto.file.maketree.MakeTreeException;
import uk.co.marcoratto.file.maketree.MakeTreeInterface;
import uk.co.marcoratto.ftp.listeners.Listener;
import uk.co.marcoratto.ftp.listeners.ListenerException;
import uk.co.marcoratto.util.RelativePath;
import uk.co.marcoratto.util.Utility;

public class FTP implements MakeTreeInterface {

	private static Logger logger = Logger.getLogger("uk.co.marcoratto.ftp");

	public static int DEFAULT_PORT = 21;

	private final static int DEFAULT_BUFFER_SIZE = 1024;
	
	private List<File> listOfFiles = new ArrayList<File>();
	
	private String server = null;
	private int port = -1;
	private String fromUri = null;
	private String toUri= null;
	private int replycode = -1;
	private FTPClient ftpClient = null;
	private long keepAliveTimeout = -1;
	private int controlKeepAliveReplyTimeout = -1;
	private boolean useEpsvWithIPv4 = false;
	private boolean listHiddenFiles = false;
	private boolean binaryTransfer = true;
	private boolean passive = true;
	private boolean isRecursive = false;
	private boolean delete = false;
	private boolean verbose = false;
	private boolean overwrite = false;
	private String username = null;
	private String wildcards = null;
	private String password = null;
	private boolean askPassword = false;
	private Listener listener = null;

	public FTP(Listener aListener) {
		this.listener = aListener;
		this.toUri = null;
		this.fromUri = null;
	}

	public void execute() throws FTPException {
		logger.info("fromUri is " + this.fromUri);
		logger.info("toUri is " + this.toUri);
		if (toUri == null) {
			throw new FTPException("Why the parameter 'toUri' is null ?");
		}
		if (fromUri == null) {
			throw new FTPException("Why the parameter 'fromUri' is null ?");
		}
		boolean isFromRemote = this.isRemoteUri(this.fromUri);
		boolean isToRemote = this.isRemoteUri(this.toUri);
		logger.info("isFromRemote is " + isFromRemote);
		logger.info("isToRemote is " + isToRemote);
		if (isFromRemote && !isToRemote) {
			logger.info("Download mode");

			String remote = this.parseUri(fromUri);
			
			if ((remote.indexOf("*") != -1)
					|| (remote.indexOf("?") != -1)) {
				logger.info("Wildcards mode");
				
				try {
					remote = new File(remote).getParentFile().getCanonicalFile().toString();
					logger.info("remote=" + remote);
	
					wildcards = new File(fromUri).getName();
					logger.info("wildcards=" + wildcards);
					
					this.connect();
					
					FTPFile[] files;

					files = this.ftpClient.listFiles(remote, new FTPFileFilter() {

								public boolean accept(FTPFile aFTPFile) {
									if (aFTPFile.isFile() && 
										FilenameUtils.wildcardMatchOnSystem(aFTPFile.getName(), wildcards)) {
										return true;
					                } else {
					                	return false;
					                }
								}										
						}
										);
					
					logger.info("Found " + files.length + " files to download.");
					for (int j=0; j<files.length; j++) {
						
						FTPFile file = files[j];
						String remoteFile = remote + "/" + file.getName();
						
			            logger.info("File do download: " + file.getName());
			            
						File local = new File(toUri);
						logger.info("The local path is " + local.getAbsolutePath());

						if (local.isDirectory()) {
							logger.info("The local path is a directory.");
							local = new File(toUri, file.getName());
							logger.info("The local path changed to " + local.getAbsolutePath());
						} else {
							throw new FTPException(local + " must to be a directory!");
						}
						
						try {
							this.listener.onStartDownload(j+1, files.length, remoteFile, local.getAbsolutePath());
						} catch (ListenerException le) {
							logger.error(le.getMessage(), le);
						}
						
						this.download(remoteFile, local);

						try {
							this.listener.onEndDownload(j+1, files.length, remoteFile, local.getAbsolutePath());
						} catch (ListenerException le) {
							logger.error(le.getMessage(), le);
						}
						if (this.delete) {
							this.delete(remoteFile);
						}
			        }
				} catch (IOException e) {
					throw new FTPException(e);
				}
			} else {
				try {
					this.listener.onStartDownload(1, 1, fromUri, toUri);
				} catch (ListenerException le) {
					logger.error(le.getMessage(), le);
				}
				
				this.connect();

				File local = new File(toUri);
				logger.info("The local path is " + local.getAbsolutePath());

				if (local.isDirectory()) {
					logger.info("The local path is a directory.");
					local = new File(toUri, new File(remote).getName());
				}

				this.download(remote, local);

				try {
					this.listener.onEndDownload(1, 1, fromUri, toUri);
				} catch (ListenerException le) {
					logger.error(le.getMessage(), le);
				}

				if (this.delete) {
					this.delete(remote);
				}

			}

		} else if (!isFromRemote && isToRemote) {
			logger.info("Upload mode");

	    	File local = null;
			try {
				local = new File(fromUri).getCanonicalFile();
			} catch (IOException e) {
				throw new FTPException(e);
			}
			logger.info("local=" + local);
			
			if (local.isFile()) {
				logger.info("File mode.");
				try {
					this.listener.onStartUpload(1, 1, local, toUri);
				} catch (ListenerException le) {
					logger.error(le.getMessage(), le);
				}

				String remote = this.parseUri(toUri);
				this.connect();
				this.upload(local, remote);
				
				try {
					this.listener.onEndUpload(1, 1, local, toUri);
				} catch (ListenerException le) {
					logger.error(le.getMessage(), le);
				}
				if (this.delete) {
					logger.info("Delete local file " + local.getAbsolutePath());
					local.delete();
				}
			} else if (local.isDirectory()) {
				logger.info("Directory mode.");
				wildcards = "*.*";
				logger.info("wildcards=" + wildcards);

				String remote = parseUri(toUri);
								
				MakeTree mt = new MakeTree(this);
				try {
					mt.searchDirectoryFile(local, wildcards, this.isRecursive);
				} catch (MakeTreeException e) {
					throw new FTPException(e);
				}

				this.connect();

				logger.info("listOfFiles has " + listOfFiles.size() + " items");
				if (!this.listOfFiles.isEmpty()) {
					for (int j=0; j<this.listOfFiles.size(); j++) {
						File f = this.listOfFiles.get(j);						
						logger.debug("ParentFile is " + f.getParentFile());
						String localRelPath = RelativePath.getRelativePath(local, f.getParentFile());
						logger.debug("localRelPath is " + localRelPath);
						
						this.createRemoteDirectoryTree(remote, localRelPath);
						
						String remoteFile = remote + "/" + localRelPath + ((localRelPath.length() == 0) ? "" : "/") + f.getName();
						try {
							this.listener.onStartUpload(j + 1, this.listOfFiles.size(), f, remoteFile);
						} catch (ListenerException le) {
							logger.error(le.getMessage(), le);
						}
						this.upload(f, remoteFile);
						try {
							this.listener.onEndUpload(j + 1, this.listOfFiles.size(), f, remoteFile);
						} catch (ListenerException le) {
							logger.error(le.getMessage(), le);
						}
						if (this.delete) {
							logger.info("Delete local file " + local.getAbsolutePath());
							local.delete();
						}
					}
				}
			} else if ((new File(fromUri).getName().indexOf("*") != -1)
					|| (new File(fromUri).getName().indexOf("?") != -1)) {
				logger.info("Wildcards mode");
				try {
					local = new File(fromUri).getParentFile().getCanonicalFile();
				} catch (IOException e) {
					throw new FTPException(e);
				}
				logger.info("local is " + local);

				wildcards = new File(fromUri).getName();
				logger.info("wildcards is " + wildcards);
				
				String remote = parseUri(toUri);
				
				MakeTree mt = new MakeTree(this);
				try {
					mt.searchDirectoryFile(local, wildcards, this.isRecursive);
				} catch (MakeTreeException e) {
					throw new FTPException(e);
				}

				this.connect();

				logger.info("listOfFiles has " + listOfFiles.size() + " items");
				if (!this.listOfFiles.isEmpty()) {
					for (int j=0; j<this.listOfFiles.size(); j++) {
						File f = this.listOfFiles.get(j);						
						logger.debug("ParentFile is " + f.getParentFile());
						String localRelPath = RelativePath.getRelativePath(local, f.getParentFile());
						logger.debug("localRelPath is " + localRelPath);
						
						this.createRemoteDirectoryTree(remote, localRelPath);
						
						String remoteFile = remote + "/" + localRelPath + ((localRelPath.length() == 0) ? "" : "/") + f.getName();
						try {
							this.listener.onStartUpload(j + 1, this.listOfFiles.size(), f, remoteFile);
						} catch (ListenerException le) {
							logger.error(le.getMessage(), le);
						}
						this.upload(f, remoteFile);
						try {
							this.listener.onEndUpload(j + 1, this.listOfFiles.size(), f, remoteFile);
						} catch (ListenerException le) {
							logger.error(le.getMessage(), le);
						}
						if (this.delete) {
							logger.info("Delete local file " + local.getAbsolutePath());
							local.delete();
						}
					}
				}
			} else {
				throw new FTPException(fromUri + " not valid!");
			}
		} else if (isFromRemote && isToRemote) {
			throw new FTPException(
					"Copying from a remote server to a remote server is not supported.");
		} else {
			throw new FTPException(
					"'toUri' and 'fromUri' attributes must have syntax like the following: user:password@host:/path");
		}
	}

	public void connect() throws FTPException {
		this.ftpClient = new FTPClient();

		if (keepAliveTimeout >= 0) {
			ftpClient.setControlKeepAliveTimeout(keepAliveTimeout);
		}
		if (controlKeepAliveReplyTimeout >= 0) {
			ftpClient
					.setControlKeepAliveReplyTimeout(controlKeepAliveReplyTimeout);
		}

		ftpClient.setListHiddenFiles(listHiddenFiles);

		// suppress login details
		if (this.verbose) {
			this.ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));			
		}

		try {
			ftpClient.connect(server, port);
			this.replycode = ftpClient.getReplyCode();

			if (!ftpClient.login(username, password)) {
				throw new FTPException("Error on login with username '"
						+ this.username + "' and password '" + this.password
						+ "'");
			}
			if (this.binaryTransfer) {				
				ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
			} else {
				ftpClient.setFileType(org.apache.commons.net.ftp.FTP.ASCII_FILE_TYPE);
			}
			if (this.passive) {
				ftpClient.enterLocalPassiveMode();
			} else {
				ftpClient.enterLocalActiveMode();
			}

			ftpClient.setUseEPSVwithIPv4(this.useEpsvWithIPv4);

			// check that control connection is working OK
			ftpClient.noop();
		} catch (FTPException e) {
			throw e;
		} catch (SocketException e) {
			throw new FTPException(e);
		} catch (IOException e) {
			throw new FTPException(e);
		} finally {
			if ((ftpClient != null)
					&& (!FTPReply.isPositiveCompletion(replycode))) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}
			}
		}
	}

	public void download(String remote, File local) throws FTPException {
		if (this.ftpClient == null) {
			throw new FTPException("ftps is null! Do you call before the method 'connect'?");
		}

		OutputStream output = null;
		try {
			if (local.exists() && this.overwrite == false) {
				throw new FTPException("Local file " + local.getPath() + " already exists!");
			} 
			String modificationTime = this.ftpClient.getModificationTime(remote);
			if (modificationTime == null) {
				throw new FTPException("Remote file " + remote + " not found!");
			} 
			output = new FileOutputStream(local);
			this.ftpClient.retrieveFile(remote, output);

		} catch (FTPException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw new FTPException(e);
		} catch (IOException e) {
			throw new FTPException(e);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}				
			}
		}
	}

	public void delete(String remote) throws FTPException {
		if (this.ftpClient == null) {
			throw new FTPException("ftps is null! Do you call before the method 'connect'?");
		}
		try {
			logger.info("Delete remote file " + remote);
			ftpClient.deleteFile(remote);
		} catch (IOException e) {
			throw new FTPException(e);
		} 
	}

	public void disconnect() throws FTPException {
		if (this.ftpClient != null) {
			try {
				if (this.ftpClient.isConnected()) {
					logger.info("Disconnect");
					this.ftpClient.disconnect();
				}
			} catch (IOException e) {
				throw new FTPException(e);
			}
		}
	}

	public boolean logout() throws FTPException {
		if (this.ftpClient == null) {
			throw new FTPException(
					"ftps is null! Do you call before the method 'connect'?");
		}
		try {
			logger.info("logout");
			return this.ftpClient.logout();
		} catch (IOException e) {
			throw new FTPException(e);
		}
	}

	public void upload(File local, String remoteFile) throws FTPException {
		if (this.ftpClient == null) {
			throw new FTPException(
					"ftps is null! Do you call before the method 'connect'?");
		}
		if (local.length() == 0) {
			throw new FTPException("Local file is empty!");
		}
		InputStream input = null;
		try {
	        long startTime = System.currentTimeMillis();
			String modificationTime = this.ftpClient.getModificationTime(remoteFile);
			logger.info("modificationTime is " + modificationTime);
			if (modificationTime != null && 
				this.overwrite == false) {
				throw new FTPException("Remote file " + remoteFile + " already exists!");
			} 
			logger.info("Upload file " + local.getAbsolutePath() + " to " + remoteFile);
			ftpClient.setBufferSize(DEFAULT_BUFFER_SIZE);
			
			input = new FileInputStream(local);
			ftpClient.storeFile(remoteFile, input);
	        long endTime = System.currentTimeMillis();
	        long delay = endTime-startTime;

	        long fileSize = Utility.getFileSize(local);
	        double average = fileSize / delay * 1000;
	        System.out.println(fileSize + " bytes sent in " + Utility.msToSecondss(delay) + " secs (" + Utility.formatDouble("0.0", average) + " kB/s)");
		} catch (FTPException e) {
			throw e;
		} catch (FileNotFoundException e) {
			throw new FTPException(e);
		} catch (IOException e) {
			throw new FTPException(e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.warn(e.getMessage(), e);
				}				
			}
		}
	}

	public int getReplycode() {
		return replycode;
	}

	public long getKeepAliveTimeout() {
		return keepAliveTimeout;
	}

	public void setKeepAliveTimeout(long value) {
		this.keepAliveTimeout = value;
		logger.info("keepAliveTimeout is " + this.keepAliveTimeout);
	}

	public int getControlKeepAliveReplyTimeout() {
		return controlKeepAliveReplyTimeout;
	}

	public void setControlKeepAliveReplyTimeout(int value) {
		this.controlKeepAliveReplyTimeout = value;
		logger.info("controlKeepAliveReplyTimeout is "
				+ this.controlKeepAliveReplyTimeout);
	}

	public boolean isListHiddenFiles() {
		return listHiddenFiles;
	}

	public void setListHiddenFiles(boolean value) {
		this.listHiddenFiles = value;
		logger.info("listHiddenFiles is " + this.listHiddenFiles);
	}

	public boolean isBinaryTransfer() {
		return binaryTransfer;
	}

	public void setBinaryTransfer(boolean value) {
		this.binaryTransfer = value;
		logger.info("binaryTransfer is " + this.binaryTransfer);
	}

	public boolean isPassive() {
		return passive;
	}

	public void setPassive(boolean value) {
		this.passive = value;
		logger.info("passive is " + this.passive);
	}

	public boolean isUseEpsvWithIPv4() {
		return useEpsvWithIPv4;
	}

	public void setUseEpsvWithIPv4(boolean value) {
		this.useEpsvWithIPv4 = value;
		logger.info("useEpsvWithIPv4 is " + this.useEpsvWithIPv4);
	}

	public boolean isRecursive() {
		return isRecursive;
	}

	public void setRecursive(boolean value) {
		this.isRecursive = value;
		logger.info("isRecursive is " + this.isRecursive);
	}

	public void setFromUri(String value) {
		this.fromUri = value;
		logger.info("fromUri is " + this.fromUri);
	}

	public void setToUri(String value) {
		this.toUri = value;
		logger.info("toUri is " + this.toUri);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int value) {
		this.port = value;
		logger.info("port is " + this.port);
	}
	
	public boolean isDelete() {
		return delete;
	}

	public void setDelete(boolean value) {
		this.delete = value;
		logger.info("delete is " + this.delete);
	}

	public boolean isVerbose() {
		return verbose;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
		logger.info("overwrite is " + this.overwrite);
	}
	
	public void setVerbose(boolean value) {		
		this.verbose = value;
		logger.info("verbose is " + this.verbose);
	}

	public void onFileFound(File aFile) throws MakeTreeException {
		logger.info("Added file " + aFile.getName());
		this.listOfFiles.add(aFile);
	}

	public void onDirFound(File aDirectory) throws MakeTreeException {
	}

	private String parseUri(String uri) throws FTPException {
		logger.info("The uri is " + uri);
		int indexOfAt = uri.indexOf('@');
		int indexOfColon = uri.indexOf(':');

		if (indexOfColon != -1 && indexOfColon < indexOfAt) {
			// user:password@host:/path notation
			// everything upto the last @ before the last : is considered
			// password. (so if the path contains an @ and a : it will not work)
			int indexOfCurrentAt = indexOfAt;
			int indexOfLastColon = uri.lastIndexOf(':');
			while (indexOfCurrentAt > -1 && indexOfCurrentAt < indexOfLastColon) {
				indexOfAt = indexOfCurrentAt;
				indexOfCurrentAt = uri.indexOf('@', indexOfCurrentAt + 1);
			}
			this.username = uri.substring(0, indexOfColon);
			this.password = uri.substring(indexOfColon + 1, indexOfAt);
		} else if (indexOfAt != -1) {
			// no password, will require keyfile
			this.username = uri.substring(0, indexOfAt);
			// this.password = "";
		}
		int indexOfPath = uri.indexOf(':', indexOfAt + 1);
		if (indexOfPath == -1) {
			throw new FTPException("no remote path in " + uri);
		}

		this.server = uri.substring(indexOfAt + 1, indexOfPath);
		String remotePath = uri.substring(indexOfPath + 1);
		logger.info("The remotePath is " + remotePath);
		
		if ((this.password == null) && 
				(this.askPassword == true)) {

				String pwd = Utility.inputString(this.username + "@" + this.server + "'s password:", null);
				logger.info("pwd is " + pwd);
				if (pwd == null) {
					throw new FTPException("no password for user "
							+ this.username + " has been "
							+ "given.  Can't authenticate.");
				} else {
					this.password = pwd;
				}
			}

		return remotePath;
	}

	private void createRemoteDirectoryTree(String startDir, String dirTree) throws FTPException {
			boolean dirExists = true;
			
	        try {
				if (!ftpClient.changeWorkingDirectory(startDir)) {
				      throw new FTPException("Unable to change into newly created remote directory '" + startDir + "'.  error='" + ftpClient.getReplyString()+"'");
				}
			} catch (IOException e) {
				throw new FTPException(e);
			}

		  //tokenize the string and attempt to change into each directory level.  If you cannot, then start creating.
		  String[] directories = dirTree.split("/");
		  for (String dir : directories ) {
		    if (!dir.isEmpty() ) {
		      if (dirExists) {
		        try {
					dirExists = this.ftpClient.changeWorkingDirectory(dir);
				    if (!dirExists) {
				    		logger.debug(" Create remote directory " + dir);
					        if (!ftpClient.makeDirectory(dir)) {
					          throw new FTPException("Unable to create remote directory '" + dir + "'.  error='" + ftpClient.getReplyString()+"'");
					        }
					        if (!ftpClient.changeWorkingDirectory(dir)) {
					          throw new FTPException("Unable to change into newly created remote directory '" + dir + "'.  error='" + ftpClient.getReplyString()+"'");
					        }
				    }
				} catch (IOException e) {
					throw new FTPException(e);
				}
		      }
		    }
		  }     
		}

	private boolean isRemoteUri(String uri) throws FTPException {
		if (uri == null) {
			throw new FTPException(
					"ERROR: Method 'isRemoteUri' receive a parameter null!");
		}
		boolean isRemote = true;
		int indexOfAt = uri.indexOf('@');
		if (indexOfAt == -1) {
			isRemote = false;
		}
		return isRemote;
	}

	public boolean isAskPassword() {
		return askPassword;
	}

	public void setAskPassword(boolean value) {
		this.askPassword = value;
		logger.info("The askPassword is " + askPassword);
	}
	
}
