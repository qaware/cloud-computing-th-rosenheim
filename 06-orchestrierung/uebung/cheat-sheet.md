# Cheat Sheet

- [minikube](#minikube)
- [kubectl](#kubectl)
- [k9s](#k9s)
- [docker](#docker)

## minikube

Cluster starten/stoppen/löschen

```shell script
minikube start
minikube stop
minikube delete
```


IP der VM ausgeben

```shell script
minikube ip
```

Addons installieren/anzeigen

```shell script
minikube addons enable <addon-name>
minikube addons enable ingress
minikube addons list
```

Version ausgeben

```shell script
minikube version
```

## kubectl

Kubernetes YAML anwenden (Do whatever it takes to make the cluster look like this)

```shell script
kubectl apply -f path/to/file.yaml
kubectl apply -f path/to/dir
```

Version ausgeben

```shell script
kubectl version
```

## k9s

Starten

```shell script
k9s
```

Navigation

- `?` Keyboard Shortcuts anzeigen
- `:deployment` Kubernetes Resource anzeigen (Bsp.: `:deployments`, `:services`, `:pods`, `:ingress`, ...)
- `/` Aktuelle Anzeige filtern
- `enter` Intelligent untergeordnete Resource / Konzept anzeigen (`service` -> `pods` -> `containers` -> `logs`)
- `esc` Raus aus: Ansicht / Kommando / Filter
- `d,v,e,l,…` Describe, View, Edit, Logs, ...

Version ausgeben

```shell script
k9s version
```

## docker

Container-Image bauen

```shell script
# docker build -t <tag> <path-to-dir-with-Dockerfile>
docker build -t my-app:1 .
```

Shell in einem laufenden Container starten

```shell script
# docker exec -it <container-name-or-id> /bin/bash
docker exec -it 066c891518fa /bin/bash
```

Neuen Container starten und Shell öffnen

```shell script
# docker run -it --entrypoint <shell> <image-name-or-id>
docker run --rm -it --entrypoint bash my-app
```

Version ausgeben

```shell script
docker version
```
