#!/bin/bash

echo ""
echo "+-------------------------------------------+"
echo "| Notification Service - build Docker image |"
echo "+-------------------------------------------+"
echo ""
docker build -t notification-service:local -f Dockerfile.svc .