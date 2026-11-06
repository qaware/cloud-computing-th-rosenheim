# Lösung 1. Pods & Deployments

Aufgaben:

4. Schreibt ein Kubernetes Deployment

siehe `01_deployment.yaml`

Bonus:

9. Ändert die Anzahl der Replikas des Deployments

   2. über `kubectl scale`
   ```shell script
   kubectl scale --replicas=2 deployment/helloservice-deployment
   ```

   3. In welcher Reihenfolge passiert das Rolling Upgrade? (z.B. Zug-um-Zug oder wird erst komplett die neue Version ausgerollt und dann die alte heruntergefahren?)
      - Zug-um-Zug / Inkrementell:
         - Starten Neu-1
         - Runterfahren Alt-1
         - Starten Neu-2
         - Runterfahren Alt-2

