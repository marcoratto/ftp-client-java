#!/bin/sh
#set -x
JAVA_OPTS="-Xms256m -Xmx1024m -XX:MaxPermSize=120m"

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

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

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    # IBM's JDK on AIX uses strange locations for the executables
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      JAVACMD="$JAVA_HOME/jre/sh/java"
    elif [ -x "$JAVA_HOME/jre/bin/java" ] ; then
      JAVACMD="$JAVA_HOME/jre/bin/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java 2> /dev/null `
    if [ -z "$JAVACMD" ] ; then
        JAVACMD=java
    fi
  fi
fi

if [ ! -x "$JAVACMD" ] 
then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit 1
fi

if [ ! -d "$HOME/.ftp" ]
then
	mkdir "$HOME/.ftp"
	mkdir "$HOME/.ftp/log"
fi

"$JAVACMD" $JAVA_OPTS -Dlog4j.warn -classpath $CPATH uk.co.marcoratto.ftp.Ftp "$@"
RET_CODE=$?
if [ $RET_CODE -ne 0 ]
then
	echo "ERROR! java return error code $RET_CODE."
fi
exit $RET_CODE
