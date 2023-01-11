# Observability

Lesen Sie sich die Datei README.md durch, um die Quarkus-Services und die Grafana-Dienste zu starten. 

## Übung 1: Logs

1. Die Services loggen im Quellcode verschiedene Dinge auf allen möglichen Leveln. Warum sehen Sie nicht alle auf der Konsole? Korrigieren Sie ggf. Einstellungen dafür.
2. Um auch komplizierte Logs, wie z.B. Stacktraces, in einem Stück zu Loki zu bringen, werden diese als JSON formatiert. Warum sehen Sie davon nichts auf der Konsole?
3. Konfigurieren Sie promtail so, dass die Logs der Services abgezogen und zu Loki gesendet werden.
4. Kontrollieren Sie die Logs im Explore-Fenster von Grafana. Was müssen Sie tun, damit statt JSON lesbare Logs angezeigt werden? 
5. Bonus: Erstellen Sie ein Grafana-Dashboard für die Logs.

Promtail-Konfiguration: https://grafana.com/docs/loki/latest/clients/promtail/configuration/

## Übung 2: Metriken

1. Sehen Sie sich die Konfiguration von Prometheus an. Welche Dienste werden von Prometheus abgefragt?
2. Quarkus stellt Metriken unter dem Pfad /q/metrics zur Verfügung. Ziehen Sie die Metriken von beiden Services mit Prometheus ab und kontrollieren Sie diese im Explore-Fenster von Grafana.
3. Mit einem schönen Dashboard lassen sich die Metriken viel besser visualisieren. Importieren Sie [dieses Dashboard von der Grafana-Website](https://grafana.com/grafana/dashboards/14370-jvm-quarkus-micrometer-metrics/) und sehen Sie sich damit die Metriken von Quarkus an.
4. Bonus: Informieren Sie sich, wie Sie selbst definierte Metriken in eine Quarkus-Anwendung einbauen können. Prometheus wird diese nun automatisch abziehen. Visualisieren Sie die Metrik in Grafana.

Prometheus-Konfiguration: https://prometheus.io/docs/prometheus/latest/configuration/configuration/ 

## Übung 3: Traces

1. In die Services ist bereits OpenTracing integriert. Machen Sie ein paar Aufrufe gegen den laufenden TLE-Service und prüfen Sie die Traces in Grafana. Wie kommen Sie an die Trace-Id?
2. Informieren Sie sich, wie Sie zusätzliche (Meta-)Daten zu den Traces hinzufügen können und Testen Sie das an einem der Services.
3. Bonus: Bauen Sie einen zweiten Service (Sie können dazu den TLE-Service klonen). Rufen Sie in dem neuen Service den TLE-Service auf und prüfen Sie den Aufruf in den Traces in Grafana. 
