# Exercise 1. Pods & Deployments

Infos:

- [Cheat-Sheet](cheat-sheet.md)

Tasks:

1. You installed 'kind' in the very first hour. Check whether the cluster is online: 

```shell
$ kubectl version     
WARNING: This version information is deprecated and will be replaced with the output from kubectl version --short.  Use --output=yaml|json to get the full version.
Client Version: version.Info{Major:"1", Minor:"26", GitVersion:"v1.26.3", GitCommit:"9e644106593f3f4aa98f8a84b23db5fa378900bd", GitTreeState:"clean", BuildDate:"2023-03-15T13:40:17Z", GoVersion:"go1.19.7", Compiler:"gc", Platform:"linux/amd64"}
Kustomize Version: v4.5.7
Server Version: version.Info{Major:"1", Minor:"25", GitVersion:"v1.25.3", GitCommit:"434bfd82814af038ad94d62ebe59b133fcb50506", GitTreeState:"clean", BuildDate:"2022-10-25T19:35:11Z", GoVersion:"go1.19.2", Compiler:"gc", Platform:"linux/amd64"}
```

2. Familiarize yourself with the Hello-Service app (see [code/hello-service](code/hello-service)).
3. Build the container image for Kubernetes (`build-to-kubernetes.sh`).
4. Write a
   [Kubernetes deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/#creating-a-deployment)
   for the `Hello-Service` app.
5. Install this deployment in the Kubernetes cluster (`kubectl apply -f`).
6. Familiarize yourself with navigating in K9s (see [Cheat-Sheet: k9s](cheat-sheet.md#k9s)).
7. Use K9s to check whether the app is starting correctly (Ready Flag, Container Logs).

Tips:

Instead of using `k9s`, you can also use `kubectl` to see the state of the cluster (e.g. with `kubectl get pods`).

If you are running Windows and are having problems with the build-to-kubernetes.sh script:
- Try running the script in [Git Bash](https://gitforwindows.org/) or WSL
. Start Git Bash with admin permissions.

Bonus:

8. Terminate a pod above `k9s` and make sure it restarts
9. Change the number of replicas of the deployment
    1. via k9s > `:deployments` > `<s> Scale`
    2. via `kubectl scale`
    3. In which order does the rolling upgrade happen? (e.g. step-by-step or is the new version rolled out completely first and then the old one shut down?)

