#!/bin/sh
export JAVA_HOME=/opt/jdk1.8.0_152
export PATH=$JAVA_HOME/bin:$PATH
nohup java -Xms2048m -Xmx2048m -Xss1m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -jar /opt/app/red-package-rain-api/red-package-rain-api-0.0.1.jar --spring.profiles.active=dev > /opt/app/red-package-rain-api/red-package-rain-api.log 2>&1 &
echo $! > /opt/app/red-package-rain-api/red-package-rain-api-service.pid
