#!/bin/sh

export LANG=zh_CN.utf8

if [ -z $JAVA_HOME]
then
	JAVA_HOME="/usr/java/jdk1.8.0_65"
fi

echo "-------------Please input your account-------------"
read -p "Username:" username
export username

read -p "Password:" -s password
export password

#调试端口
DEBUG_PORT=34343
#JAVA_DEBUG="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=${DEBUG_PORT},server=y,suspend=n"
CLASS_PATH=.:${CLASS_PATH}:./classes
JAVA_CMD="${JAVA_HOME}/bin/java ${JAVA_DEBUG} -cp ${CLASS_PATH} -Djava.ext.dirs=.:./lib:${JAVA_HOME}/jre/lib/ext com.dc.start.Start dcits-report"

echo 
nohup ${JAVA_CMD} >/dev/null 2>&1 &
sleep 3
echo 
tail -f ../logs/dcits-report/info.log
