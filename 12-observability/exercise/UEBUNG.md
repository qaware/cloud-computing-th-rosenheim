# Observability

Read the README.md file to start the Quarkus services and the Grafana services.
You can then find Grafana at the following URL: http://localhost:3000 (credentials: `admin:password123`)

## Exercise 1: Logs

1. the services log various things at all possible levels in the source code. Why don't you see them all on the console? Correct the settings for this if necessary.
2. in order to get complicated logs, such as stacktraces, in one piece to Loki, they are formatted as JSON. Why don't you see any of them on the console?
3. Configure promtail to extract the logs from the services and send them to Loki.
4. Check the logs in the Grafana Explore window. What do you need to do to display human-readable logs instead of JSON?
5. Bonus: Create a Grafana dashboard for the logs.

Promtail configuration: https://grafana.com/docs/loki/latest/clients/promtail/configuration/

## Exercise 2: Metrics

1. Take a look at the Alloy configuration. What is already happening in the config file?
2. Quarkus provides metrics under the /metrics path. Pull the metrics from both services with Alloy and check them in the Grafana Explore window.
3. With a nice dashboard, the metrics can be visualized much better. A dashboard is already configured in Grafana – how did that get there?
4. Bonus: Find out how you can incorporate self-defined metrics into a Quarkus application. Alloy will now automatically extract them. Visualize the metrics in Grafana.

Alloy configuration: https://grafana.com/docs/alloy/latest/collect/

## Exercise 3: Traces

1. OpenTelemetry is already integrated into the services. Take a look at the configuration in the application.properties of both services.
2. Make a few calls against the running sky-map service (e.g. `GET http://localhost:8088/satellite/25544/pos`) and check the traces in Grafana. How do you get the trace ID?
3. Find out how to add additional (meta) data to the traces and test this on one of the services.

Alloy configuration: https://quarkus.io/guides/opentelemetry-tracing
