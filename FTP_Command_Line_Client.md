Java Application for copying files using [FTP protocol](http://en.wikipedia.org/wiki/File_Transfer_Protocol).
This java client uses the [Apache Commons Net libraries](http://commons.apache.org/net/).

Some features:

  * Multiplatform (yes! it is written ALL in Java SE)
  * Sys logging (using `log4j-1.2.16.jar`, default log file is located on this path `$USER_HOME/.ftp/log/ftp.log`; you can change configuration on the file `$FTP_HOME/res/log4j.properties`)
  * Code coverage (EMMA + JUnit): 45.50 %

The available parameters are the following:

| **Parameter** | **Mandatory** | **Default** | **Description** |
|:--------------|:--------------|:------------|:----------------|
| **-port**     | No            | 21          | The port to connect to on the remote host |
| **-h**        | No            |             | The help        |
| **-n**        | No            | false       | Inhibit auto-login (useful for -script) |
| **-script file** | No            | false       | Run the script (default interactive/System.in) |