#!/bin/sh
PID=$(cat /opt/app/spring-cloud-security-oauth2/security-oauth2-service.pid)
kill -9 $PID
