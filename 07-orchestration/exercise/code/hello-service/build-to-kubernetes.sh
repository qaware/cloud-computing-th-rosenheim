#!/usr/bin/env bash

set -euo pipefail

# Package our service
./mvnw package

# Build container image
VERSION="1"
docker build -t "helloservice:${VERSION}" .

kind load docker-image "helloservice:${VERSION}" --name=kind
