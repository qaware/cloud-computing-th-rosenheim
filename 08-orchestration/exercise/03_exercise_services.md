# Exercise 3. Services

Infos:

- [Cheat-Sheet](cheat-sheet.md)

Tasks:

1. Create a [Kubernetes service](https://kubernetes.io/docs/concepts/services-networking/service/#defining-a-service) for the Hello Service app .
2. Start a temporary pod and use it to check that the service for the
   Hello Service can be accessed via curl using its host name.

Launching a shell in a temporary pod:

```shell script
kubectl run my-shell --rm -i --tty --image byrnedo/alpine-curl --command sh
```

Bonus:

3. Load Balancing

- Change the Hello service so that the /hello endpoint returns the local IP address (in the body or as a header).

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

Build a new version of the 'helloservice' Docker image (see 'build-to-kubernetes.sh').
Deploy the 'Hello-Service' with two replicas in the Kubernetes cluster and verify that both pods are addressed by checking the response to a 'curl' to the service.

