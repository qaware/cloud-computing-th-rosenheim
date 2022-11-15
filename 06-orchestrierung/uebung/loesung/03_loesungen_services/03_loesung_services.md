# Lösung 3. Services

Aufgaben:

1. Legt für die App `Hello-Service` einen

siehe `02_service.yaml`

2. Startet einen temporären Pod und überprüft, dass der Service erreichbar ist.

```shell script
❯ kubectl run my-shell --rm -i --tty --image byrnedo/alpine-curl --command sh
If you don't see a command prompt, try pressing enter.
/ # curl helloservice-service:8000/hello
Howdy, World!/ #
```

Bonus:

3. Load Balancing testen

Kommt bald ...

