# Übung: Faas mit AWS Lambda

In dieser Übung wollen wir AWS Lambda nutzen um FaaS kennnen zu lernen.

Anmerkung: Suchen sie sich für ihre Gruppe einen Namen aus (z.B. Grp4) und benutzen sie diesen an allen 
mit `<Name>` gekennzeichneten Stellen

Anmerkung 2: Bitte stellen sie sicher, das sie die AWS Region `eu-west-1` (Irland) benutzen. Die AWS Funktionalität ist  
anderen Regionen teilweise eingeschränkt.

Anmerkung 3: Ich benutze die Sprache Englisch (US). Diese kann links unten in der AWS-Web-Konsle umgestellt werden. 

## Aufsetzen einer ersten Cloud-Function

1. Öffnen sie die AWS Lambda Konsole: https://eu-west-1.console.aws.amazon.com/lambda/home?region=eu-west-1#/functions.
Benutzen sie dazu die bereitgestellten AWS Accounts .
   
2. Wählen sie **Create Function**

3. Füllen sie die Felder wie folgt aus
   1. Wählen sie `<Name>-function` als Name
   2. Wählen sie als Runtime `Node.js 14.x
   3. Wählen sie unter `Change default execution role` den Radiobutton `Use an existing role` 
      und wählen sie dann die Rolle `service-role/cc-2021-http-functions` aus
   3. Klicken sie auf `Create Function`
    
4. Nun sollten sie die Detailseite ihrer neuerstellten Funktion mit einen Editor der eine 
   vorgefertigte js-Funktion (index.js) enthält sehen. 

### Ausführen von Tests

1. Klicken sie in dem Editor ihrer Funktion auf `Test`. 

2. Benutzen sie als `Event name` den namen `<Name>-function-test` und klicken sie auf `Create`.

3. Führen sie den Test mit einem erneuten klick auf den Button `Test` aus. 
   Schauen sie sich das Ergebnis des Tests an.
 
### Einfache Modifikation

1. Modifizieren sie die index.js Datei im Editor so, dass z.B. ihr Gruppenname als Ergebnis ausgegeben wird. 

2. Benutzen sie `Deploy`, um ihre Änderungen einzuspielen. 

3. Führen sie den Test erneut aus und prüfen das Ergebnis.

### Monitoring

1. Wechseln sie in den Tab `Monitor` um Metriken zu ihrer Funktion einzusehen.

1. 'Spammen' sie mehrere Tests hintereinander und beobachten sie, wie sich die Metriken ändern. 
   Tipp: Evtl. müssen  sie das angezeigte Zeitintervall auf z.B. 15 minuten ändern.
   

## Einfache HTTP Funktion

In diesem Schritt werden wir HTTP Funktionalität zu der Fukntion hinzufügen. 
Ausserdem werden wir eine DynamoDB zum persistieren der Daten benutzen.

### Anpassen der Funktion

Ändern sie die komplette Funktion in index.js zu folgendem Code und deployen sie diese

    const AWS = require("aws-sdk");
    
    const dynamo = new AWS.DynamoDB.DocumentClient();
    
    exports.handler = async (event, context) => {
        let body;
        let statusCode = 200;
        const headers = {
            "Content-Type": "application/json"
        };
        
        try {
            switch (event.routeKey) {
                case "GET /books":
                    body = await dynamo.scan({ TableName: "cc-2021-lambda-table" }).promise();
                    break;
                case "PUT /books":
                    let requestBody = JSON.parse(event.body);
                    await dynamo
                      .put({
                        TableName: "cc-2021-lambda-table",
                        Item: {
                          id: requestBody.id,
                          title: requestBody.title,
                          author: requestBody.author
                        }
                      }).promise();
                    body = `Added ${requestBody.id}`;
                    break;
                default:
                    throw new Error(`Route not supported: "${event.routeKey}"`);
            }
        } catch (err) {
            body = err.message;
            statusCode = 400;
        } finally {
            body = JSON.stringify(body);
        }
    
        return {
            statusCode,
            body,
            headers
        };
    };

### Testen der angepassten Funktion

1. Estellen sie einen neuen Test mit dem Namen `<Name>-function-put` und dem folgenden Inhalt (Achtung: Ersetzen sie `<Name>` in dem Block): 
   

    {
        "routeKey": "PUT /books",
        "body": "{\"id\": \"<Name>-01\", \"title\": \"Alice in FaaS-Land\", \"Author\": \"Unknown\"}"
    }

2. Führen sie den Test aus und beobachten das Ergebnis

3. Schreiben sie enen Test mit dem Namen `<Name>-function-get`, der den GET-Fall testet.

### API Gateway konfigurieren

1. Gehen sie auf https://eu-west-1.console.aws.amazon.com/apigateway/main/apis?region=eu-west-1 (am besten in einem neuen Tab) und wählen sie `Create API`

2. Klicken sie bei `HTTP API` auf `Build`

3. Bei Integrations wählen sie `Lambda` und wählen die Lambda-Funktion ihrer Gruppe aus. Geben sie ihrer API den Namen `<Name>-api`

4. Erstellen sie 2 Routes für `GET /books` und `PUT /books` und wählen sie als integration target jeweils ihre Funktion aus.

5. Im Schritt Define stages benutzen sie die default Werte und klicken im letzten Schritt auf `Create`

7. Sie sollten nun eine Übersicht über ihr API Gateway sehen. Unter Invoke-URL steht die URL, unter der sie ihr Gateway erreichen können. 
Testen sie ihr Gateway, indem sie im Browser `<Invoke-URL>/books` aufrufen

### Tests mit CURL

Um alle Einträge zu sehen, benutzen sie den folgenden CURL-Befehl:

    curl <Invoke-URL>/books

Um einen neuen Eintrag anzulegen benutzen sie:

    curl -X "PUT" -H "Content-Type: application/json" -d "{\"id\": \"<Name>-02\", \"title\": \"CURL to AWS LAmbda\", \"Author\": \"John Doe\"}" <Invoke-URL>/books

## Erweitern der Funktion um ein GET {id]

Fügen sie in ihrer Cloud-Function einen Case `"GET /books/{id}"` ein, der ein Buch mit einer bestimmten id heraussucht. 
Mit 

    dynamo.get({
        TableName: "cc-2021-lambda-table",
        Key: {
            id: event.pathParameters.id
        }
    })

können sie ein bestimmtes Element aus der Datenbank laden. 
Das Ergebnis des Befehls ist sind die Daten des gesuchten Objekts als JS-Objekt. 

### Testen der neuen Funktion

Erstellen sie enen neuen Test mit dem Namen `<Name>-function-get-id`, der die neue funktionalität testet.

### Hinzufügen einer Route

1. Fügen sie in ihrem API Gateway die Route `GET /books/{id}` hinzu (über Routes -> Create). 

2. Fügen sie unter Integrations für die neue Route ihre Cloud-Function als Integration hinzu. Anmerkung: Falls sie mehrere Integrations sehen, nehmen sie die mit der gleichen Id wie für ihre anderen Routen.

3. Testen sie die Route mit dem folgenden CURL-Befehl:


    curl <Invoke-URL>/books/<Name>-01

### Prüfen im Monitoring

Werfen sie noch einmal einen Blick auf das Monitoring ihrer Funktion. 
Inzwischen sollten deutlich mehr daten vorhanden sein.

## Erweitern der Funktion um ein Delete

1. Fügen sie ihrer Cloud Function einen CodeBlock für den Case `DELETE /books/{id}` hinzu. Dieser soll das Element mit 
   der id `id` aus der Datenbank löschen, und im Erfolgsfall die id ausgeben. 
   Benutzen sie zum Löschen aus der DynamoDB die delete-Methode des AWS.DynamoDB.Documentclient 
   (siehe https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/DynamoDB/DocumentClient.html#delete-property)

2. Erstellen sie einen Test, der das Objekt löscht

3. Erstellen sie eine Route, mit der sie Objekte über CURL löschen können. Bitte löschen sie nicht Objekte anderer Gruppen!


######


# Optionale Übungen: Weitere Serverless Technologien

In dieser Übung wollen wir weitere verschiedene FaaS Frameworks kennenlernen.

## Kubeless

Diese Übung beschäftigt sich mit Kubeless (https://kubeless.io), einem Kubernetes
Native Serverless Framework.

1. Für die ersten Gehversuche, verwenden sie das Katacoda Tutorial von Kubeless:
https://www.katacoda.com/kubeless/

2. Installieren sie nun Kubeless lokal, entweder per Minikube oder mittels
Kubernetes von Docker. Folgen sie dabei der offiziellen Anleitung: https://kubeless.io/docs/quick-start/

3. Schreiben und deployen sie nun eine einfache Funktion in einer Sprache ihrer Wahl.
Für eine Beschreibung der verschiedenen Runtimes nutzen sie folgende Informationen:
https://github.com/kubeless/kubeless/blob/master/docs/runtimes.md

## Fn Project

Diese Übung beschäftigt sich mit Project Fn (http://fnproject.io), einer Container nativen
aber Cloud agnostischen Serverless platform.

1. Installieren sie zunächst lokal Project Fn. Folgen sie hierzu den Anweisungen der
Quickstart Dokumentation: https://github.com/fnproject/fn#top

2. Folgen sie anschließend den Anweisungen der Project Fn Tutorials. Schreiben und deployen
sie eine einfache Funktion in Java. https://fnproject.io/tutorials/JavaFDKIntroduction/

- http://fnproject.io/tutorials/
- https://github.com/fnproject/fn-helm
- https://medium.com/fnproject/fn-project-helm-chart-for-kubernetes-e97ded6f4f0c

## Serverless Framework

Diese Übung beschäftigt sich mit dem Serverless Framework (https://serverless.com/framework/),
einem CLI Tool zur einfachen und schnellen Entwicklung von von Event-getriebenen Funktionen.

Aufbauend auf den Übungen zu Kubeless und Project Fn, führen sie die Übungen erneut mit Hilfe
des Serverless Frameworks aus.

### Kubeless

Eine Übersicht zum Kubeless Provider finden sie hier: https://serverless.com/framework/docs/providers/kubeless/
Folgen sie der Quickstart Anleitung, und wenden sie diese auf die in der Kubeless
Übung erstellte Funktion an.

### Project Fn

Eine Übersicht zum Project Fn Provider finden sie hier: https://serverless.com/framework/docs/providers/fn/
Folgen sie der Quickstart Anleitung, und wenden sie diese auf die in der Project Fn
Übung erstellte Funktion an.
