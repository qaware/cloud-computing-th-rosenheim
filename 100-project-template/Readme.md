# Projekt Template

Dieses Template soll den Start in die Projektarbeit erleichtern. 
Es bringt alles mit, um den troetbot über Tilt in den lokalen cluster zu deployen. 
Außerdem wird ein ingress-controller deployed, sodass Ingress Ressourcen direkt funktionieren sollten.

## Ordnerstruktur
Erstellen Sie einen Ordner in welchem Sie Ihre Anwendung entwickeln möchten. 
Kopieren Sie den gesamten Inhalt des Templates in diesen Ordner.
Klonen Sie den [Troetbot](https://github.com/qaware/troetbot).
Wichtig ist, dass das Troetbot repository neben diesem Ordner liegt.

Die Struktur sollte also wie folgt aussehen: 
```
|
|-- your project directory
    | -- Tiltfile   
|-- Troetbot repository
```

## Deployment mit Tilt

Setzen Sie die folgenden Umgebungsvariablen: 
```shell
MASTODON_INSTANCE_HOSTNAME= <your_mastodon_instance>
MASTODON_ACCESS_TOKEN= <your_mastodon_access_token>
```

Nun können sie mit tilt den troetbot deployen: 
```shell
$ tilt up
```

Der Troetbot sollte unter http://localhost:8888 erreichbar sein. 
Wenn sie die Variablen richtig konfiguriert haben, sollten sie unter http://localhost:8888/home
ihre Mastodon Home Timeline als JSON Antwort erhalten. 

### Mastodon Access Token erhalten
1. Gehe auf settings / development.
2. Erzeuge eine neue Applikation.
3. Setze die benötigten Rechte: `read`.
4. Kopiere das Zugriffstoken

## Ingress
Wenn Sie ein Ingress-Objekt anlegen und über Tilt deployen lassen, stellen Sie sicher, dass der nginx-ingress controller bereits gestartet ist. 
Ansonsten kommt es potenziell zu unerwarteten Fehlern. 
Der folgende Snippet hilft Ihnen, die korrekte Deployment Reihenfolge einzuhalten: 
```
k8s_resource(
    objects=['your-ingress'],
    resource_deps=['nginx-ingress-controller'],
    new_name='ingress',
)
```