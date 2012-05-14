#!/bin/sh
# resolve links - $0 may be a softlink
PRG=`readlink -e $0`

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

if [ $# -ne 7 ]
then
     echo "Parameters:"
     echo "[server] [username] [password] [remote_dir] [local_dir] [files] [ascii o binary]"
 	 exit 1
fi
if [ ! -d $5 ]
then
    echo "ERROR: Local directory $5 is not valid!"
    exit 1
fi
TEMP_FILE=/tmp/ftpmput.txt

echo open $1> $TEMP_FILE
echo user $2 $3 >> $TEMP_FILE
echo cd $4>> $TEMP_FILE
echo lcd $5>> $TEMP_FILE
echo prompt>> $TEMP_FILE
echo $7 >>  $TEMP_FILE
echo mput $6>> $TEMP_FILE
echo bye>> $TEMP_FILE

echo Batch sequence:
cat $TEMP_FILE

$PRGDIR/ftp.sh -script $TEMP_FILE
RET_CODE=$?

if [ $RET_CODE -eq 0 ]
then
	rm -f $TEMP_FILE
fi
exit $RET_CODE
