#!/bin/sh
PID=$(cat /opt/app/user/user-service.pid)
kill -9 $PID
