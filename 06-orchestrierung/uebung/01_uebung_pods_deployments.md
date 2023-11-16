# Übung 1. Pods & Deployments

Infos:

- [Cheat-Sheet](cheat-sheet.md)

Aufgaben:

1. Sie haben in der allerersten Stunde `kind` installiert. Prüfen Sie, ob der Cluster online ist: 

```shell
$ kubectl version     
WARNING: This version information is deprecated and will be replaced with the output from kubectl version --short.  Use --output=yaml|json to get the full version.
Client Version: version.Info{Major:"1", Minor:"26", GitVersion:"v1.26.3", GitCommit:"9e644106593f3f4aa98f8a84b23db5fa378900bd", GitTreeState:"clean", BuildDate:"2023-03-15T13:40:17Z", GoVersion:"go1.19.7", Compiler:"gc", Platform:"linux/amd64"}
Kustomize Version: v4.5.7
Server Version: version.Info{Major:"1", Minor:"25", GitVersion:"v1.25.3", GitCommit:"434bfd82814af038ad94d62ebe59b133fcb50506", GitTreeState:"clean", BuildDate:"2022-10-25T19:35:11Z", GoVersion:"go1.19.2", Compiler:"gc", Platform:"linux/amd64"}
```

2. Machen Sie sich mit der App `Hello-Service` vertraut (siehe [code/hello-service](code/hello-service)).
3. Bauen Sie das Container-Image gegen Kubernetes (`build-to-kubernetes.sh`).
4. Schreiben Sie ein
  [Kubernetes Deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/#creating-a-deployment)
  für die App `Hello-Service`.
5. Installieren Sie dieses Deployment in den Kubernetes-Cluster (`kubectl apply -f`).
6. Machen Sie sich mit der Navigation in `k9s` vertraut (siehe [Cheat-Sheet: k9s](cheat-sheet.md#k9s)).
7. Prüfen Sie mittels `k9s`, ob die App korrekt startet (Ready Flag, Container Logs).

Tipps:

Anstatt `k9s` aufzusetzen, können Sie auch `kubectl` benutzen, um den Zustand des Clusters zu sehen (z.B. mit `kubectl get pods`).

Falls Sie unter Windows Probleme mit `build-to-kubernetes.sh` haben:
- Versuchen Sie, das Script in der [Git Bash](https://gitforwindows.org/) oder der WSL
  auszuführen. Starten Sie die Git Bash mit Admin-Rechten.

Bonus:

8. Beenden Sie einen Pod über `k9s` und stellen Sie sicher, dass er neu gestartet wird
9. Ändern Sie die Anzahl der Replikas des Deployments
   1. über k9s > `:deployments` > `<s> Scale`
   2. über `kubectl scale`
   3. In welcher Reihenfolge passiert das Rolling Upgrade? (z.B. Zug-um-Zug oder wird erst komplett die neue Version ausgerollt und dann die alte heruntergefahren?)

