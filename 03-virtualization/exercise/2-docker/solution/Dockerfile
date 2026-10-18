FROM alpine:3.24

EXPOSE 80

# Alpine's default site returns 404 for everything, so serve the bundled welcome page instead.
# Access and error logs are linked to stdout/stderr so they show up in `docker logs`.
RUN apk add --no-cache nginx curl \
    && sed -i 's|return 404;|root /var/lib/nginx/html;|' /etc/nginx/http.d/default.conf \
    && ln -sf /dev/stdout /var/log/nginx/access.log \
    && ln -sf /dev/stderr /var/log/nginx/error.log

ENTRYPOINT ["nginx"]
CMD ["-g", "daemon off;"]
