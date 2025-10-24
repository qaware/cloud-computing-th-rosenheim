#!/bin/bash
set -euo pipefail

echo "This is provision.sh 👋"

echo "installing dokku..."
wget https://raw.githubusercontent.com/dokku/dokku/v0.36.9/bootstrap.sh;
sudo DOKKU_TAG=v0.36.9 bash bootstrap.sh

echo "installing dokku postgres plugin..."
sudo dokku plugin:install https://github.com/dokku/dokku-postgres.git postgres
