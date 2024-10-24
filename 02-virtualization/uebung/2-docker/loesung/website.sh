#!/bin/bash

docker run -p 80:80 -v "$(pwd)/content:/usr/share/nginx/html" nginx:mainline
