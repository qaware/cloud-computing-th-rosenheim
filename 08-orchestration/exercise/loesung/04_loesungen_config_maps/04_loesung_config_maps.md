# Lösung 4. Config Maps

Aufgaben:

1. Erstellt eine Config Map

siehe `03_config.yaml`

2. Bindet die Config Map ein

siehe `01_deployment.yaml`

Bonus:

3. Als Alternative zum expliziten Einbinden einzelner Keys als
  Umgebungsvariable: Bindet alle
  [Keys der Config Map auf einmal](https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#configure-all-key-value-pairs-in-a-configmap-as-container-environment-variableshttps://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#configure-all-key-value-pairs-in-a-configmap-as-container-environment-variables)
  ein. Stichwort: `envFrom`.

in `01_deployment.yaml`:

```yaml
  envFrom:
    - configMapRef:
        name: helloservice-config
```

Dann muss die Config Map die Keys so enthalten, wie sie als Umgebungsvariablen erwartet werden:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: helloservice-config
  labels:
    app: helloservice
data:
  GREETING: "Howdy"
```


4. Config Map als Files

Kommt bald :)

