#!/bin/sh
#set -x
# resolve links - $0 may be a softlink
PRG=`readlink -e $0`

# Get standard environment variables
PRGDIR=`dirname "$PRG"`
LIBDIR=$PRGDIR/lib
RESDIR=$PRGDIR/res

CPATH=$PRGDIR/bin/ftp.jar
CPATH=$RESDIR:$CPATH
for FILE_JAR in `find $LIBDIR -iname "*.jar" -type f`
do
     CPATH=$FILE_JAR:$CPATH
done

$JAVA_HOME/bin/java -Dftp.home="$PRGDIR" -classpath $CPATH uk.co.marcoratto.ftp.Ftp "$@"
RET_CODE=$?
if [ $RET_CODE -ne 0 ]
then
	echo "ERROR! java return error code $RET_CODE."
	exit $RET_CODE
fi
exit 0