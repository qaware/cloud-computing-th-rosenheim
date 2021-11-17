# Lösung 2. Probes und Resource Constraints

Aufgaben:

1. Liveness und Readiness Probes

siehe `01_deployment.yaml`

2. Resource Requests und Limits

siehe `01_deployment.yaml`

Bonus:

4. Prüft, was passiert, wenn die Readiness oder Liveness Probes
  fehlschlagen

- Liveness Probe: Container wird neu gestartet. Restarts Counter wird inkrementiert.
- Readiness: Container+Pod wird als "Not Ready" markiert

5. Zu wenige oder zu viele Ressourcen

- zu wenige: Container startet nicht / crasht / wird restarted
- zu viele: Pod kann nicht gescheduled werden (Pending)

6. Startup Probe

```yaml
startupProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8000
  failureThreshold: 30
  periodSeconds: 10
```

7. Probe Zeit berechnen

- 10 Sekunden warten (`initialDelaySeconds`)
- Readiness Check 1/3 -> nach 5 Sekunden (`timeoutSeconds`) Ergebnis da: fehlgeschlagen
- 20 Sekunden warten (`periodSeconds`)
- Readiness Check 2/3 -> nach 5 Sekunden Ergebnis da: fehlgeschlagen
- 20 Sekunden warten
- Readiness check 3/3 (`failureThreshold`) -> nach 5 Sekunden Ergebnis da, fehlgeschlagen

Ergebnis: "Not Ready" nach 10 + (20 * 2) + 5 = 55 sec

