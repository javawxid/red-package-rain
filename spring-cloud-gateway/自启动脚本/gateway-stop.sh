#!/bin/sh
PID=$(cat /opt/app/spring-cloud-gateway/gateway-service.pid)
kill -9 $PID
