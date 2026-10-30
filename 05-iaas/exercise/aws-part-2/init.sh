#!/bin/bash
set -euxo pipefail

dnf install -y python3-pip nano
pip3 install cowsay

mkdir -p /var/www
{
  echo "<pre>"
  python3 -c "import cowsay; print(cowsay.get_output_string('dragon', '${message}'))"
  echo "</pre>"
} > /var/www/index.html

nohup python3 -m http.server 8080 --directory /var/www &
