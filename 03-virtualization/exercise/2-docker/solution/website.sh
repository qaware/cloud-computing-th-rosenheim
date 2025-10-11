#!/bin/bash

# mounts the content folder to the directory in the container that is served by nginx
docker run -p 80:80 -v "$(pwd)/content:/usr/share/nginx/html" nginx:mainline
