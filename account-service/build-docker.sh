#!/bin/bash

echo ""
echo "+--------------------------------------+"
echo "| Account Service - build Docker image |"
echo "+--------------------------------------+"
echo ""
docker build -t account-service:local -f Dockerfile.svc .