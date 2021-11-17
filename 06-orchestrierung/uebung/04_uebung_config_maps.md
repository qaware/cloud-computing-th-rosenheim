# Übung 4. Config Maps

Infos:

- [Cheat-Sheet](cheat-sheet.md)

Aufgaben:

1. Erstellen Sie eine
  [Config Map](https://kubernetes.io/docs/concepts/configuration/configmap/#configmaps-and-pods)
  für die App `Hello-Service`. Hinterlegen Sie eine passende Konfiguration,
  sodass der Gruß aus der Config geladen wird.
2. Binden Sie die
  [Config Map Key als Umgebungsvariable](https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#define-container-environment-variables-using-configmap-data)
  in das Deployment des `Hello-Service` ein.

Tipp: Sehen Sie sich dafür die Nutzung der Umgebungsvariable `GREETING` im
`HelloWorldController` an.

Bonus:

3. Als Alternative zum expliziten Einbinden einzelner Keys als
  Umgebungsvariable: Binden Sie alle
  [Keys der Config Map auf einmal](https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#configure-all-key-value-pairs-in-a-configmap-as-container-environment-variableshttps://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#configure-all-key-value-pairs-in-a-configmap-as-container-environment-variables)
  ein. Stichwort: `envFrom`.
4. (Vorwissen: Volumes) Als Alternative zu Umgebungsvariablen: Binden Sie die
  [Keys einer Config Map als Files](https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#populate-a-volume-with-data-stored-in-a-configmap)
  in den Container ein. Hinweis: Dafür ist natürlich auch eine Code-Anpassung nötig.

