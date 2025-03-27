#!/bin/sh
export JAVA_HOME=/opt/jdk1.8.0_152
export PATH=$JAVA_HOME/bin:$PATH
# 启动Java应用程序，并获取其PID
nohup java -Xms2048m -Xmx2048m -Xss1m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -jar /opt/app/spring-cloud-security-oauth2/spring-cloud-security-oauth2-0.0.1.jar --spring.profiles.active=dev > /opt/app/spring-cloud-security-oauth2/security-oauth2.log 2>&1 &
echo $! > /opt/app/spring-cloud-security-oauth2/security-oauth2-service.pid
