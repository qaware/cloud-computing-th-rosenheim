# Übung Service Meshes

In dieser Übung sammeln Sie Erfahrung im Umgang mit Service Meshes und ein paar der bereitgestellten Optionen. 
Wir verwenden in dieser Übung das Service Mesh [LinkerD](https://linkerd.io). 

# Cluster Setup
Für die Übung verwenden Sie einen frischen Kind-Cluster mit der bereitgestellten Konfiguration in [kind-cluster-config.yaml](code/clusterconfig/kind-cluster-config.yaml).
Starten Sie den cluster mit: 
```shell
$ kind create cluster --name cc-2023 --config kind-cluster-config.yaml
```

# Installieren Sie das Kubernetes Dashboard 
Dieser Schritt ist nicht unbedingt notwendig, hilft Ihnen aber ein besseres Verständnis zu erlangen. 

**Ziel**: Nach dieser Sektion haben Sie den Nginx Ingress Controller installiert und können das Kubernetes Dashboard lokal im Browser öffnen.

## Nginx Ingress Controller installieren

```shell
$ cd code
$ kubectl apply -f nginx-ingress-controller.yaml
```
Abhängig von Ihrer Netzwerkverbindung kann es mehrere Minuten dauern bis der Controller erfolgreich gestartet hat. 

## Dashboard installieren

Führen Sie der Reihe nach folgende Befehle aus:
```shell
$ cd code/dashboard
# Installiert das Dashboard in Kubernetes
$ kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
# Erstellt einen service account namens admin-user
$ kubectl apply -f admin-user-serviceaccount.yaml
# Erstellt ein rolebinding für den eben erstellten service account.
# Erst hiermit werden dem service account Rechte zugewiesen. In diesem Fall: cluster admin
$ kubectl apply -f admin-user-cluster-role-binding.yaml
# Legt einen Ingress für das Dashboard an.
# Dieser Schritt macht das Dashboard über den nginx ingress controller verfügbar.
$ kubectl apply -f dashboard-ingress.yaml
```

Wenn Sie alle Befehle erfolgreich ausgeführt haben, sollten sie das Dashboard im Browser unter `localhost:9999/` erreichen.
Das Dashboard verlangt einen Token zur Authentifizierung. 
Sie können mit folgendem Befehl einen Admin-Token für den angelegten serviceaccount `admin-user` generieren: 
```shell
$ kubectl -n kubernetes-dashboard create token admin-user
```

Kopieren Sie den token und fügen Sie ihn in das entsprechende Feld im Dashboard ein. 
Sie können nun den kompletten Cluster über das Dashboard inspizieren. 
Machen Sie sich mit dem Dashboard und den bereits installierten cluster komponenten vertraut.

# LinkerD CLI installieren

Folgen Sie den [Anweisungen](https://linkerd.io/2.14/getting-started/#step-1-install-the-cli) und installieren Sie die CLI. 
Wir empfehlen die Verwendung von Mac oder Linux. Auf Windows empfiehlt sich die Verwendung der WSL. 
Wenn Sie mit Windows & Powershell arbeiten wollen, müssen Sie die CLI als .exe [hier](https://github.com/linkerd/linkerd2/releases/tag/stable-2.14.4) herunterladen.

Es empfiehlt sich, die CLI auf den Pfad zu legen, um leichter arbeiten zu können.

Prüfen sie, ob die CLI korrkt installiert wurde:
```shell
$ linkerd version
```

Sie sollten die CLI version sehen können. Die `Server version` sollte momentan noch einen Fehler anzeigen, da linkerd noch nicht im Cluster installiert ist.

# Setup validieren

Validieren Sie Ihr setup mit: 

```shell
$ linkerd check --pre
``` 

Sie sollten keine Fehler sehen.

# LinkerD in den Cluster installieren

Installieren Sie zuerst die Custom Resource Definitions mit folgendem Befehl: 
```shell
$ linkerd install --crds | kubectl apply -f -
```

Installieren Sie die LinkerD Control Plane mit folgendem Befehl: 
```shell
$ linkerd install | kubectl apply -f -
```

Verfolgen Sie die Installation im Dashboard. Finden Sie heraus, welche Pods die Control Plane bilden.

Prüfen Sie das Setup mit folgendem Befehl: 
```shell
$ linkerd check
```

Sie sollten nach erfolgreicher Installation keine Fehler sehen.

# LinkerD Observability
LinkerD kommt mit einem optionalen Observability Stack namens `Viz`, der es dem Nutzer deutlich vereinfacht zu sehen, wie LinkerD im Cluster arbeitet. 
Installieren Sie den Stack mit: 
```shell
$ linkerd viz install | kubectl apply -f - 
```

Prüfen Sie, ob alles korrekt installiert wurde mit: 
```shell
$ linkerd check
```

Öffnen Sie das `Viz` Dashboard im Browser und verschaffen sich einen Überblick mit: 
```shell
$ linkerd viz dashboard &
```
Sie werden feststellen, dass derzeit nur die LinkerD Control Plane und Viz Extension Pods gemeshed sind.

# Installation der Demo Anwendung
Wir installieren für Testzwecke die LinkerD Demo Anwendung Emojivoto, die sie Emojis bewerten lässt.
Die Anwendung kann mit folgendem Befehl installiert werden: 
```shell
$ curl --proto '=https' --tlsv1.2 -sSfL https://run.linkerd.io/emojivoto.yml \
  | kubectl apply -f -
```

Die Anwendung bietet ein Web Frontend an, das mit einem port-forwarding im Browser erreichbar ist:
```shell
$ kubectl -n emojivoto port-forward svc/web-svc 8080:80
```
Die Anwendung ist jetzt im Browser unter `localhost:8080/` erreichbar. 
Machen Sie sich mit der Anwendung vertraut. Gelegentlich werden Sie Fehler in der Anwendung feststellen. 
Diese sind beabsichtigt. 

Im `Viz` Dashboard sollten sie die Anwendung sehen können. Bisher ist sie allerdings nicht gemeshed und es gibt entsprechend auch keine Metriken zu sehen. 

# Meshen der Demo Anwendung
LinkerD meshed Pods, wenn gewisse Annotation auf dem Deployment oder dem Namespace vorhanden sind. 
Mit folgendem Befehl können Sie Emojivoto meshen: 
```shell
kubectl get -n emojivoto deploy -o yaml \
  | linkerd inject - \
  | kubectl apply -f -
```

Die Applikations-Pods sollten automatisch durchstarten.
Anschließend sollte die Anwendung im `Viz` Dashboard als gemeshed angezeigt werden. 
Sie können nun auch Metriken bzgl. des Traffics der Applikation sehen.

Durch das meshen der Pods haben Sie automatisch auch die mTLS Verschlüsselung von LinkerD für die Anwendung aktiviert. 
Jeglicher Traffic zwischen meshed Pods wird transparent e2e verschlüsselt, ohne Anpassung an der Applikation selbst. 

# Authorization Policies
Mit Authorization Policies kann service-service Kommunikation abgesichert werden. 
Dabei werden Regeln festgelegt, die im Sidecar Proxy umgesetzt werden.
Das feature funktioniert auf Basis der Pod Identität und funktioniert entsprechend auch wieder ohne Anpassungen in der Anwendung selbst.

LinkerD bietet zur Konfiguration von Authorization Policies mehrere Custom Resources an. 
Zunächst erstellt man eine policy für ein Backend, bzw. einen Server.
Legen Sie folgende Authorization Policy an: 
```yaml
apiVersion: policy.linkerd.io/v1beta1
kind: Server
metadata:
  namespace: emojivoto
  name: voting-grpc
  labels:
    app: voting-svc
spec:
  podSelector:
    matchLabels:
      app: voting-svc
  port: grpc
  proxyProtocol: gRPC
```

Die Policy deckt den port namens `grpc` des Voting-Services ab. 
Prüfen Sie, was die Policy bewirkt hat. Funktioniert Emojivoto noch wie erwartet?

Sie können auch über die CLI den Status von Authorization prüfen, z.B. mit: 
```shell
$ linkerd viz authz -n emojivoto deploy/voting
```

Sie haben sicherlich festgestellt, dass das voting backend nicht mehr erreichbar ist. 
In der derzeitigen Konfiguration müssen erlaubte clients explizit angegeben werden. 
Dazu legen sie eine ServerAuthorization an: 
```yaml
apiVersion: policy.linkerd.io/v1beta1
kind: ServerAuthorization
metadata:
  namespace: emojivoto
  name: voting-grpc
  labels:
    app.kubernetes.io/part-of: emojivoto
    app.kubernetes.io/name: voting
    app.kubernetes.io/version: v11
spec:
  server:
    name: voting-grpc
  # The voting service only allows requests from the web service.
  client:
    meshTLS:
      serviceAccounts:
        - name: web
```

Nun sollten die Requests vom `web` service and das `voting` backend wieder funktionieren. 
Prüfen Sie, ob sie den Service erfolgreich ohne Mesh aufrufen können, z.B. mit:

```shell
$ kubectl run grpcurl --rm -it --image=networld/grpcurl --restart=Never --command -- ./grpcurl -plaintext voting-svc.emojivoto:8080 emojivoto.v1.VotingService/VoteDog
```

# Mesh all the things (optional)
Meshen Sie den nginx ingress controller und das kubernetes dashboard. 
Sichern Sie die Kommunikation mit Authorization Policies.

# Explore LinkerD (optional)
LinkerD hat noch einiges mehr zu bieten. 
Machen Sie sich mit weiteren Features des Service Meshes vertraut. 
Ein passender Einstieg wären die folgenden: 
- https://linkerd.io/2.14/tasks/configuring-retries/
- https://linkerd.io/2.14/tasks/circuit-breakers/
- https://linkerd.io/2.14/tasks/distributed-tracing/
