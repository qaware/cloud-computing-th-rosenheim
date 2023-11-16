# Übung 3. Services

Infos:

- [Cheat-Sheet](cheat-sheet.md)

Aufgaben:

1. Legen Sie für die App `Hello-Service` einen
  [Kubernetes Service](https://kubernetes.io/docs/concepts/services-networking/service/#defining-a-service)
  an.
2. Starten Sie einen temporären Pod und überprüfen Sie aus diesem heraus, dass der Service für den
  `Hello-Service` per `curl` über seinen Hostnamen erreichbar ist.

Starten einer Shell in einem temporären Pod

```shell script
kubectl run my-shell --rm -i --tty --image byrnedo/alpine-curl --command sh
```

Bonus:

3. Load Balancing testen

- Bauen Sie den `Hello-Service` so um, dass der `/hello` Endpunkt die lokale IP Adresse zurückgibt (im Body oder als Header).

```java
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloWorldController {
    @GetMapping("/hello")
    public String hello(HttpServletRequest clientRequest) {
        String localIp = clientRequest.getLocalAddr();
        // ...
    }
}
```

- Bauen Sie eine neue Version des `helloservice` Docker Images (siehe `build-to-kubernetes.sh`).
- Deployen Sie den `Hello-Service` mit zwei Replikas in den Kubernetes Cluster und verifizieren Sie über die Antwort auf einen `curl` auf den Service, dass beide Pods angesprochen werden.

