#!/bin/bash

echo ""
echo "+------------------------------------+"
echo "| Order Service - build Docker image |"
echo "+------------------------------------+"
echo ""
docker build -t order-service:local -f Dockerfile.svc .