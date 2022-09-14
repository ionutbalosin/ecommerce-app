#!/bin/bash

echo ""
echo "******************************************************"
echo "* [1/3] Compile and package all the services locally *"
echo "******************************************************"
echo ""

BASE_DIR="${PWD}"

./mvnw package -DskipTests

echo ""
echo "*************************************************"
echo "* [2/3] Build Docker images for all the service *"
echo "*************************************************"
echo ""

cd ./product-service
./build-docker.sh
cd "${BASE_DIR}"

cd ./shopping-cart-service
./build-docker.sh
cd "${BASE_DIR}"

echo ""
echo "******************************************************************"
echo "* [3/3] Start all the service (and the dependencies) with Docker *"
echo "******************************************************************"
echo ""

docker-compose -f ./product-service/docker-compose.yml \
               -f ./shopping-cart-service/docker-compose.yml up \
               -d --remove-orphans