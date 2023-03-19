#!/bin/bash

echo ""
echo "******************************************"
echo "* [1/3] Compile and package all services *"
echo "******************************************"
echo ""

BASE_DIR="${PWD}"

if ! ./mvnw package -DskipTests; then
    echo ""
    echo "Error: Maven encountered errors, unable to continue!"
    exit
fi

echo ""
echo "*************************************************"
echo "* [2/3] Build Docker images for all services *"
echo "*************************************************"
echo ""

cd ./account-service
./build-docker.sh
cd "${BASE_DIR}"

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
echo "***************************************************************"
echo "* [3/3] Start all services (and their dependencies) with Docker *"
echo "***************************************************************"
echo ""

docker-compose -f ./docker-compose-kafka.yml \
               -f ./docker-compose-postgres.yml \
               -f ./docker-compose-traefik.yml \
               -f ./docker-compose-spring-boot.yml \
               up --scale product-service=2 \
               -d --remove-orphans

echo ""
echo "Congratulations, everything was successful!"