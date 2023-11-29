# Observability

Lesen Sie sich die Datei README.md durch, um die Quarkus-Services und die Grafana-Dienste zu starten. 
Sie finden Grafana anschließend unter folgender URL: http://localhost:3000 (Credentials: `admin:password123`)

## Übung 1: Logs

1. Die Services loggen im Quellcode verschiedene Dinge auf allen möglichen Leveln. Warum sehen Sie nicht alle auf der Konsole? Korrigieren Sie ggf. Einstellungen dafür.
2. Um auch komplizierte Logs, wie z.B. Stacktraces, in einem Stück zu Loki zu bringen, werden diese als JSON formatiert. Warum sehen Sie davon nichts auf der Konsole?
3. Konfigurieren Sie promtail so, dass die Logs der Services abgezogen und zu Loki gesendet werden.
4. Kontrollieren Sie die Logs im Explore-Fenster von Grafana. Was müssen Sie tun, damit statt JSON lesbare Logs angezeigt werden? 
5. Bonus: Erstellen Sie ein Grafana-Dashboard für die Logs.

Promtail-Konfiguration: https://grafana.com/docs/loki/latest/clients/promtail/configuration/

## Übung 2: Metriken

1. Sehen Sie sich die Konfiguration des Grafana Agents an. Was passiert bereits im Config-File?
2. Quarkus stellt Metriken unter dem Pfad /metrics zur Verfügung. Ziehen Sie die Metriken von beiden Services mit dem Grafana Agent ab und kontrollieren Sie diese im Explore-Fenster von Grafana.
3. Mit einem schönen Dashboard lassen sich die Metriken viel besser visualisieren. Im Grafana ist bereits ein Dashboard konfiguriert - wie kommt das da rein?
4. Bonus: Informieren Sie sich, wie Sie selbst definierte Metriken in eine Quarkus-Anwendung einbauen können. Der Grafana Agent wird diese nun automatisch abziehen. Visualisieren Sie die Metrik in Grafana.

Grafana-Agent-Konfiguration: https://grafana.com/docs/agent/latest/flow/ 

## Übung 3: Traces

1. In die Services ist bereits OpenTracing integriert. Sehen Sie sich die Konfiguration dazu in den application.properties der beiden Services an. 
2. Machen Sie ein paar Aufrufe gegen den laufenden sky-map-Service (z.B. `GET http://localhost:8088/satellite/25544/pos`) und prüfen Sie die Traces in Grafana. Wie kommen Sie an die Trace-Id?
3. Informieren Sie sich, wie Sie zusätzliche (Meta-)Daten zu den Traces hinzufügen können und Testen Sie das an einem der Services. 

Agent-Konfiguration: https://quarkus.io/guides/opentracing
