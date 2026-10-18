# get alpine
docker pull alpine:latest
# start in an interactive terminal session, i.e. you have an open shell inside the container
docker run -it alpine:latest /bin/sh

# inside the container
whoami
apk update
apk add nginx curl
# Alpine's default site returns 404 for everything, serve the bundled welcome page instead
sed -i 's|return 404;|root /var/lib/nginx/html;|' /etc/nginx/http.d/default.conf
nginx
curl localhost
exit

# back on the host: find the ID of the exited container and save its state as a new image
docker ps -a
docker commit <CONTAINER ID> cloudcomputing/nginx
docker images

# start nginx in the foreground with port 80 mapping, i.e. port 80 on host maps to port 80 in the container
docker run -d -p 80:80 cloudcomputing/nginx nginx -g "daemon off;"

# in a different terminal window
curl "localhost:80"
