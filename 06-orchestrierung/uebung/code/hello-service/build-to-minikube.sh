#!/usr/bin/env bash

set -euo pipefail

# Package our service
./mvnw package

# Point current shell to minikube's docker-daemon
eval "$(minikube -p minikube docker-env)"

# Build container image
VERSION="1"
docker build -t "helloservice:${VERSION}" .
