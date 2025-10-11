# get alpine
docker pull alpine:latest
# start in an interactive terminal session, i.e. you have an open shell inside the container
docker run -it alpine:latest /bin/sh

# start nginx in the forgreound with port 80 mapping, i.e. port 80 on host maps to port 80 in the container
docker run -d -p 80:80 cloudcomputing/nginx nginx -g "daemon off;"

# in a different terminal window
curl "localhost:80"

