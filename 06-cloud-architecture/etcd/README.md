# Exercise: etcd

Documentation `ectdctl`: https://etcd.io/docs/v3.6/dev-guide/

1. Install the most recent verions of `etcdctl` on your system: https://github.com/etcd-io/etcd/releases
2. Start the etcd-Cluster with `docker compose up -d`.
3. Have a look to the etcd-Cluster:

```shell
$ docker ps                                          
CONTAINER ID   IMAGE                  COMMAND                  CREATED          STATUS          PORTS                                                   NAMES
7ff698f8fcbb   bitnami/etcd:3.6.4       "/opt/bitnami/etcd/b…"   37 minutes ago   Up 37 minutes   2380/tcp, 0.0.0.0:23792->2379/tcp, :::23792->2379/tcp   etcd-etcd-2-1
781cc8e9a056   bitnami/etcd:3.6.4       "/opt/bitnami/etcd/b…"   37 minutes ago   Up 37 minutes   2380/tcp, 0.0.0.0:23791->2379/tcp, :::23791->2379/tcp   etcd-etcd-1-1
f40bcfb1920b   bitnami/etcd:3.6.4       "/opt/bitnami/etcd/b…"   37 minutes ago   Up 37 minutes   2380/tcp, 0.0.0.0:23793->2379/tcp, :::23793->2379/tcp   etcd-etcd-3-1
```

4. As an example, check the status of the first node:

```shell
$ etcdctl --endpoints=localhost:23791 endpoint status
localhost:23791, 2848abbfff24c332, 3.6.4, 3.6.0, 20 kB, 16 kB, 20%, 0 B, false, false, 3, 12, 12, , , false
```

5. Set some values for the first etcd-Node. Example:

```shell
$ etcdctl --endpoints=localhost:23791 put lecture "Cloud Computing WS 2025/26"
OK
```

6. Subscribe to the changes for one key on one of the nodes. What happens when you change the value on one of the other nodes? Can you use that to see the complete version history for one key?

```shell
$ etcdctl --endpoints=localhost:23792 watch lecture
```

7. Request the values on the first node. What happens when you send you request to another node?

```shell
$ etcdctl --endpoints=localhost:23791 get lecture
...
```

8. Override the values multiple times with new values. Which value is now stored as consensus in the cluster? Can you also see the previous values?
9. Stop one of the etcd-Nodes and repeat put and get with new values. What happens after you restart the node?

```shell
$ docker stop etcd-etcd-1-1

...

$ docker start etcd-etcd-1-1
```

10. What happens if you stop two or three of the nodes?
