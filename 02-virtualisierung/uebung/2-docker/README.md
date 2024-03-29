# Übung: VirtualiSierung mit Docker

## Vorbereitung

Dieses Tutorial benötigt eine Docker-Installation auf ihrem lokalen System.

Zum Test, ob Docker richtig funktioniert, `docker run hello-world` ausführen. 

## Aufgaben Teil 1

1. Starten Sie einen Docker Container auf Basis des *alpine* Images und lassen Sie darin eine interaktive Konsole als Entrypoint-Prozess laufen. Auf Docker Hub https://hub.docker.com finden Sie das benötigte Basis-Image.
2. Geben Sie 'whoami' ein um herauszufinden, als welcher User Sie nun eingeloggt sind. root ist dabei der Nutzer mit den höchsten Privilegien. Sie sind aber nur root innerhalb dieses Namespace.
3. InitialiSieren Sie die Paketmanager *apk* mit `apk update`
4. Installieren Sie den NGINX Webserver mit `apk add nginx` und analog das curl Paket.
5. Starten Sie den NGINX Webserver mit dem Befehl `nginx`. Hinweis: Sollte dabei der Fehler _open() "/run/nginx/nginx.pid" failed_ auftreten, dann bitte das entsprechende Verzeichnis erstellen mit `mkdir /run/nginx`. 
6. Lassen Sie sich mit dem `curl` Befehl die Website auf der Kommandozeile ausgeben, die der NGINX Webserver auf *localhost* Port 80 zur Verfügung stellt. Überprüfen Sie, ob eine Begrüßungs-Website von NGINX ausgegeben wird.
7. Prüfen Sie, ob Sie im Host System ebenfalls auf die Seite zugreifen können. Das sollte nicht der Fall sein.
8. Steigen Sie aus der Bash im Docker Container per `exit` Befehl aus. Wo befinden Sie sich nun?
9. Lassen Sie sich alle von Docker verwalteten Container anzeigen mit `docker ps –a` und ermitteln Sie die Container-Id des gerade erzeugten Containers. Warum sehen Sie den Container nicht mit `docker ps`?

### Bonus

1. Erstellen Sie ein Image aus dem erzeugten Container mit dem Befehl `docker commit <CONTAINER ID> cloudcomputing/nginx`
2. Lassen Sie sich mit `docker images` alle von Docker verwalteten Images anzeigen und prüfen Sie, ob das soeben erzeugte Image mit dabei ist.
3. Starten Sie den neu erzeugten NGINX Container im Daemon-Modus (docker Container startet im Hintergrund) und leiten Sie dabei den Gast-Port 80 auf den Host-Port 80 weiter. Der Entrypoint-Prozess ist dabei NGINX. NGINX muss dabei aber im Vordergrund, also im Nicht-Daemon-Modus laufen, da ansonsten der Container sofort beendet würde: `nginx -g "daemon off;"`
4. Lassen Sie sich alle laufenden Docker Container per `docker ps` anzeigen. 
5. Lassen Sie sich per `curl` die Ausgaben auf *localhost* unter den Ports 80 ausgeben. Sie können nun auch auf die NGINX-Seiten direkt aus einem Browser des Haupt-Betriebssystems ihres Rechners zugreifen. Über welche URL ist dies möglich? 
6. Inspizieren Sie die Docker-Container mit `docker inspect` und lassen Sie sich die Systemlogs in den Containern per `docker logs` ausgeben.

## Aufgaben Teil 2

In den ersten Schritten haben wir das Image manuell erstellt und mussten außerdem für nginx Startparameter über die Kommandozeile angeben. 

1. Nutzen Sie explizit als Basisimage alpine:3.11. Hier wird automatisch die letzte Bugfixversion mit der Hauptversion 3.11 runtergeladen. Zum Beispiel 3.11.6.
2. Schreiben Sie ein sog. Dockerfile*` und bauen Sie damit automatisch das cloudcomputing/nginx Image.
3. Verwenden Sie dazu die Kommandos "RUN, EXPOSE, ENTRYPOINT oder CMD"
4. Bauen Sie das Docker image mit `docker build -t cloudcomputing/nginx .` 
5. Starten Sie das docker image mit `docker run -d -p 80:80 cloudcomputing/nginx`.
6. Prüfen Sie sie korrekte Funktion mit curl.

## Aufgaben Teil 3

Wir wollen jetzt noch unser eigenes HTML von nginx ausliefern lassen. Dazu schieben wir eine HTML-Datei in das Verzeichnis, in dem der NGINX-Webserver solche Artefakte erwartet und nutzen dazu Volumes.

Wir verwenden jetzt das offizielle NGINX-Docker-Image (`docker run -p 80:80 nginx:mainline`). 
Was müssen Sie noch ändern, damit der NGINX-Webserver die Demo-Webseite aus dem Verzeichnis `content` ausliefern kann?
Tipp: Im Container müssen die Dateien im Verzeichnis `/usr/share/nginx/html` liegen.

## Aufräumen

Docker kann viel Platz auf ihrer Festplatte verbrauchen. Für ein schnelles Aufräumen aller ungenutzten Ressourcen kann der Befehl `docker system prune` verwendet werden.

## Fundierter Einstieg in Docker

Für die Vorlesung wird empfohlen einen tieferen Einstieg in Docker zu machen, als dies im Rahmen der Übung möglich ist. Nutzen Sie hierfür den Docker-Kurs auf Killercoda: https://killercoda.com/brian/course/docker-basics. Arbeiten Sie die folgenden Szenarien im Sinne einer Hausaufgabe durch:
* Docker commands
* Docker storage
* Docker network
* Dockerfile

## Quellen

Diese Übung soll auch eine eigenständige Problemlösung auf Basis von Informationen aus dem Internet vermitteln. Sie können dazu für die eingesetzten Technologien z.B. die folgenden Quellen nutzen:
* Die Dokumentation von Docker: https://docs.docker.com
* Ein interaktives Tutorial zu Docker: https://www.docker.com/tryit
* Eine Übersicht der wichtigsten Docker Befehle: https://github.com/wsargent/docker-cheat-sheet
* Die Referenz zur Docker CLI und zum Dockerfile: https://docs.docker.com/reference/
