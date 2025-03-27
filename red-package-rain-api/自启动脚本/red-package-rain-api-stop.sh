#!/bin/sh
PID=$(cat /opt/app/red-package-rain-api/red-package-rain-api-service.pid)
kill -9 $PID
