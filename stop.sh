#!/bin/sh

echo "正在停止自动报工系统..."

kill `ps -ef | grep java | grep com.dc.start.Start | awk '{print $2}'`

sleep 3

pcount=`ps -ef | grep java | grep com.dc.start.Start | wc -l`
if [ $pcount -eq 0 ]
then
        echo "自动报工系统已停止！"
fi