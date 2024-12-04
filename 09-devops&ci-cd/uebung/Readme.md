# GitOps mit Flux

In dieser Übung wollen wir GitOps mit Flux in Kubernetes kennen lernen. 
Wir werden Flux lokal in den Kind Cluster installieren und dann auf dem GitOps Weg Applikationen deployen. 
Zu guter Letzt, deployen wir das Weave-GitOps-Dashboard aka die Flux UI. 

## Voraussetzungen

Erstelle ein public repository auf Github. Du kannst das repo nach der Übung wieder löschen. 
Erstelle einen Personal Access Token, der 
- Schreibzugriff auf das Repository hat
- SSH deployment keys hinzufügen darf

## Flux CLI installieren
Flux besteht aus einer CLI und mehreren Server-Komponenten im Kubernetes.
Für die Installation wird die Flux CLI benötigt. 
Folgen Sie der [Anleitung](https://fluxcd.io/flux/get-started/#install-the-flux-cli) und installieren Sie die CLI. 
Folgen Sie nachfolgend dem Flux [Getting Started Guide](https://fluxcd.io/flux/get-started/#export-your-credentials). 
Sie sollten nach Abschluss dieses Abschnitts 
- die Flux CLI installiert haben
- Flux in Kind installiert haben
- ihr public repository als cluster state verwenden
- die PodInfo Applikation mit Flux deployed haben

## Flux UI installieren
In diesem Abschnitt wollen wir die Flux UI installieren. 
Dafür wird eine weitere CLI benötigt, die `gitops` CLI. 
Installieren Sie die CLI nach folgender [Anleitung](https://docs.gitops.weave.works/docs/next/open-source/getting-started/install-OSS/#install-the-gitops-cli).

Die CLI sollte auf dem Pfad liegen, um nachfolgende Schritte einfacher zu gestalten. 

Innerhalb Ihres cluster state repositories führen Sie dann folgenden Befehl aus: 
```shell
PASSWORD="pick your password"
gitops create dashboard ww-gitops \
  --password=$PASSWORD \
  --export > ./clusters/my-cluster/weave-gitops-dashboard.yaml
```

Das erstellt Ihnen ein Manifest, dass mittels des Helm-Controllers die Weave-UI deployed.
Committen und Pushen Sie Ihre Änderungen. 

Es kann etwas dauern, bis alles deployed wurde. 
Bei Erfolg, sollte folgender Befehl: 
```shell
kubectl get pods -n flux-system
```

ungefähr so aussehen: 
```shell
NAME                                       READY   STATUS    RESTARTS   AGE
helm-controller-5bfd65cd5f-gj5sz           1/1     Running   0          10m
kustomize-controller-6f44c8d499-s425n      1/1     Running   0          10m
notification-controller-844df5f694-2pfcs   1/1     Running   0          10m
source-controller-6b6c7bc4bb-ng96p         1/1     Running   0          10m
ww-gitops-weave-gitops-86b645c9c6-k9ftg    1/1     Running   0          5m
```

Nun können Sie auf die UI über ein Port-Forwarding zugreifen. 
```shell
kubectl port-forward svc/ww-gitops-weave-gitops -n flux-system 9001:9001
```

Öffnen Sie die UI im Browser unter localhost:9001. 
Loggen Sie sich mit username `admin` und dem zuvor gewählten Passwort ein. 
Machen Sie sich mit der UI vertraut. 

Versuchen Sie beispielsweise herauszufinden warum der PodInfo HorizontalPodAutoscaler nicht richtig funktioniert.