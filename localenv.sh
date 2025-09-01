#!/bin/bash

docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)

docker run --name rococo-all \
--network rococo-net \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=secret \
  -v mysqldata:/var/lib/mysql \
  -v ./mysql/script:/docker-entrypoint-initdb.d \
  -e TZ=GMT+3 \
  -d mysql:8.0 \
  --max-prepared-stmt-count=1000

docker run --name zookeeper \
--network rococo-net \
  -e ZOOKEEPER_CLIENT_PORT=2181 \
  -p 2181:2181 \
  -d confluentinc/cp-zookeeper:7.3.2

docker run --name kafka \
--network rococo-net \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  -p 9092:9092 \
  -d confluentinc/cp-kafka:7.3.2