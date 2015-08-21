Java Application for copying files using [FTP](http://en.wikipedia.org/wiki/File_Transfer_Protocol).
This java client uses the Apache Commons IO.

Some features:

  * Multiplatform (yes! it is written ALL in Java SE)
  * Sys logging (using `log4j-1.2.16.jar`, default log file is located on this path `$USER_HOME/.ftp/log/ftp.log`; you can change configuration on the file `$FTP_HOME/res/log4j.properties`)
  * Listener API (Java interface `uk.co.marcoratto.ftp.listeners.Listener`, see [Listeners](Listeners.md) for more details)

You can choose on of the following tool:
  * [ftp](http://code.google.com/p/ftp-client-java/wiki/ftp)
  * [FTP Command-Line Client](http://code.google.com/p/ftp-client-java/wiki/FTP_Command_Line_Client)

[![](http://www2.clustrmaps.com/stats/maps-no_clusters/code.google.com-p-ftp-client-java--thumb.jpg)](http://www2.clustrmaps.com/user/e8710a778)