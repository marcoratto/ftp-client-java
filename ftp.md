Some features:

  * Multiplatform (yes! it is written ALL in Java SE)
  * Sys logging (using `log4j-1.2.16.jar`, default log file is located on this path `$USER_HOME/.ftp/log/ftp.log`; you can change configuration on the file `$FTP_HOME/res/log4j.properties`)
  * Listener API (Java interface `uk.co.marcoratto.ftp.listeners.Listener`, see [Listeners](Listeners.md) for more details)
  * Code Coverage (EMMA + JUnit): 75.70%

The available parameters are the following:

| **Parameter** | **Mandatory** | **Default** | **Description** |
|:--------------|:--------------|:------------|:----------------|
| **-source**   | Yes           | n/d         | The source directory. This can be a local path or a remote path of the form `user[:password]@host:/directory/path`. :password can be omitted if you use key based authentication or specify the password attribute. The way remote path is recognized is whether it contains @ character or not. This will not work if your localPath contains @ character.|
| **-target**   | Yes           | n/d         | The target directory. This can be a local path or a remote path of the form `user[:password]@host:/directory/path`. :password can be omitted if you use key based authentication or specify the password attribute. The way remote path is recognized is whether it contains @ character or not. This will not work if your localPath contains @ character.|
| **-port**     | No            | 21          |The port to connect to on the remote host.|
| **-r**        | No            | false       | Search the file recursively.|
| **-ask**      | No            | false       | Ask to digit the password.|
| **-b**        | No            | false       | The file transfer is binary mode (default to false, ASCII). |
| **-d**        | No            | false       | Delete remote file (download) or local file (upload) after download or upload. |
| **-o**        | No            | false       | Overwrite target file if exists, else error (default false). |
| **-e**        | No            | false       | Use EPSV with IPv4. |
| **-k secs**   | No            | -1          | Use keep-alive timer, setControlKeepAliveTimeout. |
| **-w msec**   | No            | -1          | Wait time for keep-alive reply, setControlKeepAliveReplyTimeout (default 1). |
| **-retry #**  | No            | 0           | Number of retry in case of errors. |
| **-r**        | No            | false       | Traverse recursive the directory and send the files (default false).|