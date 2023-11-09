# Übung: etcd

Dokumentation `ectdctl`: https://etcd.io/docs/v3.5/dev-guide/

1. Installieren Sie die aktuelle Version von `etcdctl` auf Ihrem System: https://github.com/etcd-io/etcd/releases
2. Starten Sie das etcd-Cluster mit `docker compose up -d`.
3. Sehen Sie sich das etcd-Cluster an:

```shell
$ docker ps                                          
CONTAINER ID   IMAGE                  COMMAND                  CREATED          STATUS          PORTS                                                   NAMES
7ff698f8fcbb   bitnami/etcd:3.5       "/opt/bitnami/etcd/b…"   37 minutes ago   Up 37 minutes   2380/tcp, 0.0.0.0:32823->2379/tcp, :::32823->2379/tcp   etcd-etcd-2-1
781cc8e9a056   bitnami/etcd:3.5       "/opt/bitnami/etcd/b…"   37 minutes ago   Up 37 minutes   2380/tcp, 0.0.0.0:32824->2379/tcp, :::32824->2379/tcp   etcd-etcd-1-1
f40bcfb1920b   bitnami/etcd:3.5       "/opt/bitnami/etcd/b…"   37 minutes ago   Up 37 minutes   2380/tcp, 0.0.0.0:32822->2379/tcp, :::32822->2379/tcp   etcd-etcd-3-1
```

4. Prüfen Sie beispielhaft für den ersten Node seinen Status:

```shell
$ etcdctl --endpoints=localhost:23791 endpoint status
localhost:23791, 2848abbfff24c332, 3.5.10, 20 kB, true, false, 3, 12, 12,
```

5. Setzen Sie einige Werte in den ersten etcd-Node. Beispiel:

```shell
$ etcdctl --endpoints=localhost:23791 put vorlesung "Cloud Computing WS 2023/24"
OK
```

6. Abbonieren Sie an einem der Nodes Änderungen auf einen Key. Was passiert jetzt, wenn Sie den Wert an einem anderen Node ändern? Können Sie das nutzen, um die komplette Versionsgeschichte zu einem Key zu sehen?

```shell
$ etcdctl --endpoints=localhost:23792 watch vorlesung
```

7. Fragen Sie Ihre Werte am selben Node ab. Was passiert, wenn Sie die Anfrage an einen anderen Node stellen?

```shell
$ etcdctl --endpoints=localhost:23791 get vorlesung
...
```

8. Überschreiben Sie die Werte mehrfach mit neuen Werten. Welcher Wert steht jetzt als Konsens im Cluster? Können Sie sich auch die vorigen Werte ansehen?
9. Stoppen Sie einen der etcd-Nodes und wiederholen Sie put und get mit neuen Werten. Was passiert, nachdem Sie den Node wieder starten?

```shell
$ docker compose stop etcd-1

...

$ docker compose start etcd-1
```

10. Was passiert, wenn Sie zwei von drei Nodes stoppen?
