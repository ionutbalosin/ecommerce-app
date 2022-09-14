#!/bin/bash

echo ""
echo "+--------------------------------------+"
echo "| Product Service - build Docker image |"
echo "+--------------------------------------+"
echo ""
docker build -t product-service:local -f Dockerfile.svc .