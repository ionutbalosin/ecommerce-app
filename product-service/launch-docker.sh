#!/bin/bash

echo "*****************************"
echo "* eCommerce Product Service *"
echo "*****************************"

echo "Build the Docker image"
docker build -t eclipse-temurin:17-jdk -f Dockerfile.svc .

echo "Start the service (and the dependencies) with Docker"
docker-compose -f docker-compose.yml up -d --remove-orphans