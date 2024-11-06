#!/bin/bash
set -euxo pipefail

apt-get update
apt-get install -y busybox cowsay
rm -rf /var/lib/apt/lists/*

{
  echo "<pre>"
  /usr/games/cowsay -f dragon ${message}
  echo "</pre>"
} >> index.html

nohup busybox httpd -f index.html -p 8080 &
