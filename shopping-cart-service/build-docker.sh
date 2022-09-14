#!/bin/bash

echo ""
echo "+--------------------------------------------+"
echo "| Shopping Cart Service - build Docker image |"
echo "+--------------------------------------------+"
echo ""
docker build -t shopping-cart-service:local -f Dockerfile.svc .