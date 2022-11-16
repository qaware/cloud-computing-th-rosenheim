# Bonus Übung 5. Ingress

Infos:

- [Cheat-Sheet](cheat-sheet.md)
- Hinweis: Damit ingress in Minikube aktivert ist, müssen sie das Addon "ingress" installieren. Siehe den Befehl `minikube addons enable ingress` bzw. [Minikube Ingress](https://kubernetes.io/docs/tasks/access-application-cluster/ingress-minikube/). 

Aufgaben:

1. Legen Sie für den `Hello-Service` einen
  [Ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/#the-ingress-resource)
  an.
2. Prüfen Sie, ob der Service von außerhalb des Clusters erreichbar ist
  (`minikube ip`).

