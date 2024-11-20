# Übung 2. Probes und Resource Constraints

Infos:

- [Cheat-Sheet](cheat-sheet.md)

Aufgaben:

1. Bauen Sie in das Deployment der App `Hello-Service` Liveness- und
  Readiness-Probes ein, siehe
  [HTTP Probes](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/#define-a-liveness-http-request).
2. Vergeben Sie
  [Resource Requests und Limits](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/#resource-units-in-kubernetes).
3. Prüfen Sie mittels `k9s`, ob die App korrekt startet.

Bonus:

4. Prüfen Sie, was passiert, wenn die Readiness oder Liveness Probes fehlschlagen.
5. Prüfen Sie, was passiert, wenn zu wenige oder zu viele Ressourcen angefragt werden.
   1. Verifizieren Sie, dass kein Pod gescheduled werden kann (Pending).
   2. Nutzen Sie die `<d>` Describe Funktion in k9s, um die Events des Pods anzusehen.
6. Bauen Sie eine
  [Startup-Probe](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/#define-startup-probes)
  ein.

7. Probe Zeit berechnen

Angenommen:

  - die Liveness Probe des Containers funktioniert und
  - der Request auf den Endpunkt `/actuator/health/readiness` läuft in einen Timeout.

Wie lange dauert es, bis eine App mit der nachfolgenden Readiness Probe als "Not Ready" markiert wird?

```yaml
  readinessProbe:
    httpGet:
      path: /actuator/health/readiness
      port: 8000
    initialDelaySeconds: 10
    periodSeconds: 20
    failureThreshold: 3
    timeoutSeconds: 5
```

