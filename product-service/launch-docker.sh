#!/bin/bash

echo "*****************************"
echo "* eCommerce Product Service *"
echo "*****************************"
echo ""

echo "Build the Docker image"
echo ""
docker build -t product-service -f Dockerfile.svc .

echo ""
echo "Start the service (and the dependencies) with Docker"
docker-compose -f docker-compose.yml up -d --remove-orphans