# Cheat Sheet

- [kubectl](#kubectl)
- [k9s](#k9s)
- [docker](#docker)

## kubectl

Apply Kubernetes YAML (do whatever is necessary to make the cluster look like this)

```shell script
kubectl apply -f path/to/file.yaml
kubectl apply -f path/to/dir
```

Print Version

```shell script
kubectl version
```

## k9s

Start

```shell script
k9s
```

Navigation

- `?` Show keyboard shortcuts
- `:deployment` Show Kubernetes resource (e.g.: `:deployments`, `:services`, `:pods`, `:ingress`, ...)
- `/` Filter current screen
- `enter` Intelligent sub resource / show concept (`service` -> `pods` -> `containers` -> `logs`)
- `esc` Get out of: View / Command / Filter
- `d,v,e,l,…` Describe, View, Edit, Logs, ...

Show version

```shell script
k9s version
```

## docker

Build container image

```shell script
# docker build -t <tag> <path-to-dir-with-Dockerfile>
docker build -t my-app:1 .
```

Start Shell in a running container

```shell script
# docker exec -it <container-name-or-id> /bin/bash
docker exec -it 066c891518fa /bin/bash
```

Start a new container and open a shell

```shell script
# docker run -it --entrypoint <shell> <image-name-or-id>
docker run --rm -it --entrypoint bash my-app
```

Print version

```shell script
docker version
```
