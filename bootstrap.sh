#!/bin/bash
# eCommerce Application
#
# Copyright (c) 2022 - 2023 Ionut Balosin
# Website: www.ionutbalosin.com
# Twitter: @ionutbalosin / Mastodon: ionutbalosin@mastodon.socia
#
#
# MIT License
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

echo ""
echo "******************************************"
echo "* [1/3] Compile and package all services *"
echo "******************************************"
echo ""

BASE_DIR="${PWD}"

if ! ./mvnw package -DskipTests; then
    echo ""
    echo "ERROR: Maven encountered errors and cannot continue."
    exit
fi

echo ""
echo "**********************************************"
echo "* [2/3] Build Docker images for all services *"
echo "**********************************************"
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

cd ./shipping-service
./build-docker.sh
cd "${BASE_DIR}"

cd ./notification-service
./build-docker.sh
cd "${BASE_DIR}"

echo ""
echo "*****************************************************************"
echo "* [3/3] Start all services (and their dependencies) with Docker *"
echo "*****************************************************************"
echo ""

docker-compose -f ./docker-compose-kafka.yml \
               -f ./docker-compose-postgres.yml \
               -f ./docker-compose-debezium.yml \
               -f ./docker-compose-traefik.yml \
               -f ./docker-compose-spring-boot.yml \
               up --scale product-service=2 \
               -d --remove-orphans

echo ""
echo "Congratulations! Everything was successful."