# Übung 1. Pods & Deployments

Infos:

- [Cheat-Sheet](cheat-sheet.md)

Aufgaben:

1. Starten Sie Docker und minikube (`minikube start`).
2. Machen Sie sich mit der App `Hello-Service` vertraut (siehe [code/hello-service](code/hello-service)).
3. Bauen Sie das Container-Image gegen Minikube (`build-to-minikube.sh`).
4. Schreiben Sie ein
  [Kubernetes Deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/#creating-a-deployment)
  für die App `Hello-Service`.
5. Installieren Sie dieses Deployment in den Kubernetes-Cluster (`kubectl apply -f`).
6. Machen Sie sich mit der Navigation in `k9s` vertraut (siehe [Cheat-Sheet: k9s](cheat-sheet.md#k9s)).
7. Prüfen Sie mittels `k9s`, ob die App korrekt startet (Ready Flag, Container Logs).

Tipps:

Anstatt `k9s` aufzusetzen, können Sie auch `kubectl` benutzen, um den Zustand des Clusters zu sehen (z.B. mit `kubectl get pods`).

Sollte es zu einem `ImagePullErr` kommen, obwohl dein Docker gegen
Minikube konfiguriert ist, versuche ein `imagePullPolicy: Never` unter
`spec.containers.`

Falls Sie unter Windows Probleme mit `build-to-minikube.sh` haben:
- Versuchen Sie, das Script in der [Git Bash](https://gitforwindows.org/)
  auszuführen. Starten Sie die Git Bash mit Admin-Rechten.
- Unter Windows kann es sein, dass `minikube docker-env` einen Befehl in
  der letzten Zeile ausgibt, der mit REM anfängt. Dieses REM ist nicht
  Teil des Befehls, den Sie eingeben müssen! Geben Sie alles hinter REM in die
  Konsole ein.

Bonus:

8. Beenden Sie einen Pod über `k9s` und stellen Sie sicher, dass er neu gestartet wird
9. Ändern Sie die Anzahl der Replikas des Deployments
   1. über k9s > `:deployments` > `<s> Scale`
   2. über `kubectl scale`
   3. In welcher Reihenfolge passiert das Rolling Upgrade? (z.B. Zug-um-Zug oder wird erst komplett die neue Version ausgerollt und dann die alte heruntergefahren?)

