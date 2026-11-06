# Exercise 4. Config Maps

Infos:

- [Cheat-Sheet](cheat-sheet.md)

Tasks:

1. Create a
   [Config Map](https://kubernetes.io/docs/concepts/configuration/configmap/#configmaps-and-pods)
   for the Hello-Service app. Store a suitable configuration so that
   the greeting is loaded from the config.
2. Bind the
   [Config Map Key as an environment variable](https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#define-container-environment-variables-using-configmap-data)
   in the deployment of the Hello service.

Tip: See how the environment variable GREETING is used in
HelloWorldController.

Bonus:

3. As an alternative to explicitly integrating individual keys as
   environment variables: Integrate all
   [config map keys at once](https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#configure-all-key-value-pairs-in-a-configmap-as-container- environment-variableshttps://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#configure-all-key-value-pairs-in-a-configmap-as-container-environment-variables)
   ein. Keyword: `envFrom`.
4. (Previous knowledge: Volumes) As an alternative to environment variables: Bind the
   [keys of a config map as files](https://kubernetes.io/docs/tasks/configure-pod-container/configure-pod-configmap/#populate-a-volume-with-data-stored-in-a-configmap)
   into the container. Note: This also requires a code adjustment, of course.


