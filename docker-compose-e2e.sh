#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"

export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)

docker compose down
docker_containers=$(docker ps -a -q)
docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'rococo')


MODE=$1
if [ "$MODE" = "test-only" ]; then
  echo "### Running in TEST-ONLY mode ###"
  autotest_container=$(docker ps -a -q --filter "ancestor=${PREFIX}/rococo-e-2-e-tests:latest")
  if [ -n "$autotest_container" ]; then
    echo "### Stop old autotest container: $autotest_container ###"
    docker stop $autotest_container
    docker rm $autotest_container
  fi

  echo "### Rebuilding e2e tests image ###"
  ./gradlew :rococo-e-2-e-tests:clean
  docker build -t ${PREFIX}/rococo-e-2-e-tests:latest -f ./rococo-e-2-e-tests/Dockerfile .

  echo "### Starting test profile containers ###"
  docker compose --profile test up -d
  docker ps -a
  exit 0
fi


if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

if [ ! -z "$docker_images" ]; then
  echo "### Remove images: $docker_images ###"
  docker rmi $docker_images
fi

echo '### Java version ###'
java --version
bash ./gradlew clean
bash ./gradlew jibDockerBuild -x :rococo-e-2-e-tests:test

docker pull selenoid/vnc_chrome:127.0
docker compose up -d
docker ps -a
