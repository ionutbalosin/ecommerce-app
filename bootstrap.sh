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

cd ./order-service
./build-docker.sh
cd "${BASE_DIR}"

cd ./payment-service
./build-docker.sh
cd "${BASE_DIR}"

echo ""
echo "*******************************************************************"
echo "* [3/3] Start all the services (and the dependencies) with Docker *"
echo "*******************************************************************"
echo ""

docker-compose -f ./docker-compose-kafka.yml -f ./docker-compose-microservices.yml up -d --remove-orphans

echo ""
echo "Congratulations, everything was successful!"