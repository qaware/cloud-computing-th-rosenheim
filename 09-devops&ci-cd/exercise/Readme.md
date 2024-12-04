# GitOps with Flux

In this exercise, we will get to know GitOps with Flux in Kubernetes.  
We will install Flux locally in the Kind cluster and then deploy applications using the GitOps approach.  
Finally, we will deploy the Weave GitOps Dashboard, also known as the Flux UI.

## Prerequisites

- Create a public repository on GitHub. You can delete the repository after the exercise.
- Create a Personal Access Token with the following permissions:
    - Write access to the repository.
    - Permission to add SSH deployment keys.

## Installing the Flux CLI

Flux consists of a CLI and several server components in Kubernetes.  
The Flux CLI is required for installation.  
Follow the [instructions](https://fluxcd.io/flux/get-started/#install-the-flux-cli) to install the CLI.

Next, follow the Flux [Getting Started Guide](https://fluxcd.io/flux/get-started/#export-your-credentials).  
By the end of this section, you should have:
- Installed the Flux CLI.
- Installed Flux in Kind.
- Configured your public repository as the cluster state.
- Deployed the PodInfo application using Flux.

## Installing the Flux UI

In this section, we will install the Flux UI.  
This requires another CLI, the `gitops` CLI.  
Unfortunately, the CLI only works on Linux and Mac.  
Windows users should proceed to the `Windows` section.

### Mac & Linux

Install the CLI by following [this guide](https://github.com/weaveworks/weave-gitops).  
Ensure the CLI is in your PATH for easier usage in the following steps.

Within your cluster state repository, execute the following command:
```shell
PASSWORD="pick your password"
gitops create dashboard ww-gitops \
  --password=$PASSWORD \
  --export > ./clusters/my-cluster/weave-gitops-dashboard.yaml
```

This generates a manifest that uses the Helm Controller to deploy the Weave UI.  
Commit and push your changes.

### Windows Only

This section can be ignored by Mac and Linux users.

Copy the file [weave-gitops-dashboard.yaml](../solution/clusters/my-cluster/weave-gitops-dashboard.yaml) to the same location in your repository.  
Commit and push your changes.

### Continue Here After the OS-Specific Section

It may take some time for everything to deploy.  
Once successful, the following command:
```shell
kubectl get pods -n flux-system
```

Should produce output similar to this:
```shell
NAME                                       READY   STATUS    RESTARTS   AGE
helm-controller-5bfd65cd5f-gj5sz           1/1     Running   0          10m
kustomize-controller-6f44c8d499-s425n      1/1     Running   0          10m
notification-controller-844df5f694-2pfcs   1/1     Running   0          10m
source-controller-6b6c7bc4bb-ng96p         1/1     Running   0          10m
ww-gitops-weave-gitops-86b645c9c6-k9ftg    1/1     Running   0          5m
```

You can now access the UI via port forwarding:
```shell
kubectl port-forward svc/ww-gitops-weave-gitops -n flux-system 9001:9001
```

Open the UI in your browser at `localhost:9001`.  
Log in using the username `admin` and the password you set earlier.  
Windows users should use the password `admin`.
Familiarize yourself with the UI.

For example, try to find out why the PodInfo HorizontalPodAutoscaler is not working correctly.  

## Install the kubernetes dashboard via flux
Just like the last time in the service meshes exercise you should install the kubernetes dashboard using flux. 
The flux helm controller can work with helm charts. 
Deploy the kubernetes dashboard via the flux helm controller. 

Docs for getting you started:
https://fluxcd.io/flux/guides/helmreleases/