"""Minimal web application for the PaaS exercise.

Uses only the Python standard library, so the container image stays small.

Endpoints:
  /        HTML page showing the app version and which task served the request
  /health  health check endpoint used by the load balancer
"""

import datetime
import html
import os
import socket
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

PORT = int(os.environ.get("PORT", "8080"))
APP_VERSION = os.environ.get("APP_VERSION", "v1")
GREETING = os.environ.get("GREETING", "Hello from the cloud!")
STARTED_AT = datetime.datetime.now(datetime.timezone.utc)

PAGE = """<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>PaaS Exercise</title>
  <style>
    body {{ font-family: sans-serif; max-width: 40rem; margin: 4rem auto; padding: 0 1rem; }}
    .card {{ border: 2px solid {color}; border-radius: 8px; padding: 1.5rem; }}
    h1 {{ color: {color}; }}
    td {{ padding: 0.25rem 1rem 0.25rem 0; }}
  </style>
</head>
<body>
  <div class="card">
    <h1>{greeting}</h1>
    <table>
      <tr><td>Version</td><td><b>{version}</b></td></tr>
      <tr><td>Served by (hostname)</td><td>{hostname}</td></tr>
      <tr><td>Container started at</td><td>{started}</td></tr>
    </table>
  </div>
</body>
</html>
"""

# TODO (Task 6): change the color to see your new version go live
COLOR = "#e47911"


class Handler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/health":
            self._send(200, "text/plain", "OK")
            return

        if self.path == "/":
            body = PAGE.format(
                color=COLOR,
                greeting=html.escape(GREETING),
                version=html.escape(APP_VERSION),
                hostname=html.escape(socket.gethostname()),
                started=STARTED_AT.strftime("%Y-%m-%d %H:%M:%S UTC"),
            )
            self._send(200, "text/html; charset=utf-8", body)
            return

        self._send(404, "text/plain", "Not Found")

    def _send(self, status, content_type, body):
        data = body.encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)


if __name__ == "__main__":
    print(f"Starting app {APP_VERSION} on port {PORT}", flush=True)
    ThreadingHTTPServer(("0.0.0.0", PORT), Handler).serve_forever()
