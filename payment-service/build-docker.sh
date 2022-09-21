#!/bin/bash

echo ""
echo "+--------------------------------------+"
echo "| Payment Service - build Docker image |"
echo "+--------------------------------------+"
echo ""
docker build -t payment-service:local -f Dockerfile.svc .