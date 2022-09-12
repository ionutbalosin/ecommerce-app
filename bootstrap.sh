#!/bin/bash

echo "****************************************"
echo "* (1/2) Build all the services locally *"
echo "****************************************"
echo ""

PWD="${PWD}"
./mvnw package

echo "*************************************************************************"
echo "* (2/2) Start all the services (including the dependencies) with Docker *"
echo "*************************************************************************"
echo ""

# product service
cd ./product-service
./launch-docker.sh
cd $PWD