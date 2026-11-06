# Exercise 2. Probes and Resource Constraints

Infos:

- [Cheat-Sheet](cheat-sheet.md)

Tasks:

1. Include liveness and readiness probes in the deployment of the 'Hello-Service' app
   , see
   [HTTP Probes](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/#define-a-liveness-http-request).
2. Assign
   [Resource Requests and Limits](https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/#resource-units-in-kubernetes).
3. Use k9s to check whether the app starts correctly.

Bonus:

4. Check what happens when the readiness or liveness probes fail.
5. Check what happens when too few or too many resources are requested.
    1. Verify that no pod can be scheduled (pending).
    2. Use the <d>Describe function in k9s to view the pod's events.
6. Build a
   [Startup-Probe](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/#define-startup-probes)
   .

7. calculate probe delays

Assumed:

- the liveness probe of the container is working and
- the request to the endpoint `/actuator/health/readiness` times out.

How long does it take for an app to be marked as “Not Ready” with the subsequent readiness probe?

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

