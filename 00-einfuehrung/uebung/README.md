# Übung: Setup

Ziel der heutigen Übung ist es, die benötigten Softwarekomponenten für die weiteren Übungen auf Ihren Rechnern zu installieren.

## Installationen

1. Git ([Installation](https://git-scm.com/downloads))
2. Ein Java 17 JDK (z.B. [Azul Zulu JDK](https://www.azul.com/downloads/?package=jdk).
3. Eine Java IDE ihrer Wahl, z.B. [IntelliJ Community Edition](https://www.jetbrains.com/de-de/idea/download/)
4. Docker ([Windows](https://docs.docker.com/docker-for-windows/install/), [Mac](https://docs.docker.com/docker-for-mac/install/), [Linux](https://docs.docker.com/engine/install/))
5. Kind (https://kind.sigs.k8s.io/docs/user/quick-start#installation)
6. Tilt (https://docs.tilt.dev)
7. Kubectl (https://kubernetes.io/docs/tasks/tools/)

## Test des Setups

1. Öffnen Sie eine Console in einem Ordner Ihrer Wahl und geben Sie `git clone https://github.com/qaware/cloudcomputing.git` ein
2. Öffnen Sie den Ordner [jdk-test-1](jdk-test-1/) in einer Console und geben Sie `mvn clean install` (`./mvnw clean install` unter Mac und Linux) ein
3. Öffnen Sie den Ordner [jdk-test-2](jdk-test-2/) in einer Console und geben Sie `gradlew clean build` (`./gradlew clean build` unter Mac und Linux) ein
4. Importieren Sie die [jdk-test-1/pom.xml](jdk-test-1/pom.xml) in IntelliJ und starten Sie die `main`-Methode
5. Importieren Sie die [jdk-test-2/build.gradle](jdk-test-2/build.gradle) in IntelliJ und starten Sie die `main`-Methode
6. Testen Sie [ihre Docker-Installation](https://docs.docker.com/get-started/#test-docker-version)

## Test des lokalen Kubernetes clusters
> **Hinweis**
> Die nachfolgenden Schritte erfordern eine funktionale Docker Installation!

Öffnen Sie eine Console und navigieren Sie in den Ordner [kubernetes-cluster-setup](./kubernetes-cluster-setup). 

Erzeugen Sie einen lokalen Kind cluster mit dem Befehl:
```shell
$ kind create cluster --name cc-2023 --config kind-cluster-config.yaml                                                                                                                           ─╯
```
Dies kann beim ersten mal mehrere Minuten dauern. 
Prüfen Sie mit folgendem Befehl, ob der cluster erfolgreich erstellt wurde: 
```shell
$ kind get clusters
# expected output --> cc-2023
```
Sie sollten einen cluster mit dem Namen `cc-2023` sehen. 
Prüfen Sie mit folgendem Befehl, ob drei container gestartet wurden und ohne Neustarts laufen: 
```shell
$ docker ps
```
Die Ausgabe sollte ungefähr so aussehen: 
```
CONTAINER ID   IMAGE                  COMMAND                  CREATED          STATUS          PORTS                                             NAMES
ffc91bee66e0   kindest/node:v1.27.3   "/usr/local/bin/entr…"   45 seconds ago   Up 42 seconds                                                     cc-2023-worker2
bb8e1ac8987d   kindest/node:v1.27.3   "/usr/local/bin/entr…"   45 seconds ago   Up 42 seconds   0.0.0.0:9999->80/tcp, 127.0.0.1:52492->6443/tcp   cc-2023-control-plane
edc7c37d988d   kindest/node:v1.27.3   "/usr/local/bin/entr…"   45 seconds ago   Up 42 seconds                                                     cc-2023-worker
```

Deployen Sie nun mit folgendem Befehl die Test Workloads in den cluster:
```shell
$ tilt up
```
Folgen Sie den Anweisungen auf dem Bildschirm und öffnen die Tilt UI im Browser.
Alle Workloads sollten erfolgreich starten und nach kurzer Zeit einen stabilen Zustand erreichen.

Zuletzt wollen wir sicherstellen, dass auch wie erwartet mit dem cluster kommuniziert werden kann. 
Dafür rufen Sie im Browser die URL `localhost:9999/foo/hostname` auf. Sie sollten als Antwort `foo-app` erhalten.
Rufen Sie nun im Browser die URL `localhost:9999/bar/hostname` auf. Sie sollten als Antwort `bar-app` erhalten.

Sie können den cluster nun wieder herunterfahren. 
Führen Sie hierfür folgenden Befehl aus:
```shell
$ kind delete cluster --name cc-2023
```