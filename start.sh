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

CLASS_PATH=.:${CLASS_PATH}:./classes
JAVA_CMD="${JAVA_HOME}/bin/java -cp ${CLASS_PATH} -Djava.ext.dirs=./lib:${JAVA_HOME}/jre/lib/ext com.dc.start.Start"

nohup ${JAVA_CMD} >/dev/null 2>&1 &
sleep 3
echo ""
tail -f ../log/info.log
