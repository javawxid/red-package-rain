#!/bin/sh
PID=$(cat /opt/app/red-package-rain-server/red-package-rain-server-service.pid)
kill -9 $PID
