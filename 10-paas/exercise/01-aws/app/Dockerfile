FROM python:3.13-alpine

WORKDIR /app
COPY server.py .

# Run as an unprivileged user
RUN adduser -D -u 10001 app
USER app

ENV PORT=8080
EXPOSE 8080

CMD ["python", "-u", "server.py"]
