#!/bin/bash

echo ""
echo "+--------------------------------------+"
echo "| Shipping Service - build Docker image |"
echo "+--------------------------------------+"
echo ""
docker build -t shipping-service:local -f Dockerfile.svc .