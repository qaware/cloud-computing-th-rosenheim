# Exercise: Service Meshes

In this exercise, you will gain experience with service meshes and some of the provided options.  
We will use the service mesh [Linkerd](https://linkerd.io) in this exercise and roughly follow its [Getting Started guide](https://linkerd.io/docs/getting-started/).

The exercise was tested with the following versions:

| Tool | Version |
| --- | --- |
| [kind](https://kind.sigs.k8s.io/) | v0.33.0 (Kubernetes v1.37.0) |
| [Gateway API](https://gateway-api.sigs.k8s.io/) | v1.5.1 |
| [Traefik](https://traefik.io/) (Helm chart) | v3.7.20 (42.4.3) |
| [Headlamp](https://headlamp.dev/) (Helm chart) | v0.46.0 (0.46.0) |
| [Linkerd](https://linkerd.io) | edge-26.11.2 |

# Prerequisites
All commands in this exercise are meant to be executed in this directory (`10-service-meshes/exercise`).

Make sure the following tools are installed:
- [Docker](https://docs.docker.com/get-docker/) (or another container runtime supported by kind)
- [kind](https://kind.sigs.k8s.io/docs/user/quick-start/#installation) in version v0.33.0 or newer, e.g. `brew install kind`
- [kubectl](https://kubernetes.io/docs/tasks/tools/)
- [helm](https://helm.sh/docs/intro/install/), which is basically a package manager for Kubernetes components, e.g. `brew install helm`

We recommend using Mac or Linux. If you are on Windows, consider using WSL.

# Cluster Setup
For this exercise, use a fresh kind cluster with the provided configuration in [kind-cluster-config.yaml](code/clusterconfig/kind-cluster-config.yaml).  
Start the cluster with:
```shell
$ kind create cluster --name cc-2026 --config code/clusterconfig/kind-cluster-config.yaml
```

Check that the cluster runs Kubernetes v1.37:
```shell
$ kubectl version
```

# Install the Gateway API
To make applications inside the cluster accessible from the outside, Kubernetes used to offer the `Ingress` API.
The `Ingress` API is frozen, and the popular Ingress NGINX controller has been retired.
Its successor is the [Gateway API](https://gateway-api.sigs.k8s.io/), which consists of resources such as `GatewayClass`, `Gateway` and `HTTPRoute`.
The Gateway API is not part of Kubernetes itself, but is installed as a set of Custom Resource Definitions (CRDs).

Linkerd also uses the Gateway API resources, e.g. for authorization policies and dynamic request routing.
Thus, the installed Gateway API version must be [compatible with Linkerd](https://linkerd.io/docs/features/gateway-api/).
At the time of writing, Linkerd supports Gateway API versions up to v1.5.1, so we do **not** install the latest version.

Check whether the Gateway API is already installed in the cluster:
```shell
$ kubectl get crds/httproutes.gateway.networking.k8s.io -o "jsonpath={.metadata.annotations.gateway\.networking\.k8s\.io/bundle-version}"
```

A `NotFound` error means that the Gateway API is not yet installed. Install it with:
```shell
$ kubectl apply --server-side -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v1.5.1/standard-install.yaml
```

# Install the Kubernetes Dashboard Headlamp
This step is not strictly necessary but will help you to get a better understanding of what's happening inside Kubernetes when installing a service mesh.

**Objective**: After this section, you will have installed the Traefik gateway and can open the Kubernetes dashboard [Headlamp](https://headlamp.dev/) locally in the browser.

## Install the Traefik Gateway
The Gateway API only defines resources. An implementation, the gateway controller, is needed that actually routes the traffic.
We use [Traefik](https://doc.traefik.io/traefik/reference/install-configuration/providers/kubernetes/kubernetes-gateway/) for this.

Install Traefik with Helm and the provided [values](code/gateway/traefik-values.yaml):
```shell
$ helm repo add traefik https://traefik.github.io/charts
$ helm repo update
$ helm upgrade --install traefik traefik/traefik --version 42.4.3 \
    --namespace traefik --create-namespace \
    -f code/gateway/traefik-values.yaml
```

> **Note:** We pin Traefik v3.7 (chart 42.4.3) on purpose. Traefik v3.8 requires Gateway API v1.7, which is not yet supported by Linkerd.

The Helm chart creates a `GatewayClass` named `traefik` and a `Gateway` named `traefik-gateway` in the `traefik` namespace.
The gateway is exposed as `NodePort` service on port `30080`, which the kind cluster config maps to port `9999` on your machine.
Depending on your network connection, it may take a minute until Traefik is running.
Check that the gateway is ready, i.e. `PROGRAMMED` is `True`:
```shell
$ kubectl get gatewayclass
$ kubectl get gateway -n traefik
```

## Install Headlamp

Execute the following commands in sequence:
```shell
# Add the headlamp repository
$ helm repo add headlamp https://kubernetes-sigs.github.io/headlamp/
$ helm repo update
# Deploy a Helm release named "headlamp" using the headlamp chart
$ helm upgrade --install headlamp headlamp/headlamp --version 0.46.0 --namespace headlamp --create-namespace
# Creates an HTTPRoute for Headlamp.
# This step makes Headlamp accessible through the Traefik gateway.
$ kubectl apply -f code/headlamp/headlamp-httproute.yaml
```

Have a look at [headlamp-httproute.yaml](code/headlamp/headlamp-httproute.yaml) and compare it to an `Ingress` resource.
Check that the route was accepted by the gateway:
```shell
$ kubectl describe httproute -n headlamp headlamp
```

If you have executed all commands successfully, you should be able to access Headlamp in the browser at [localhost:9999](http://localhost:9999/).  
Headlamp requires a token for authentication.  
The Helm chart created a service account named `headlamp`, which is bound to the `cluster-admin` cluster role.
You can generate a token for this service account with the following command:
```shell
$ kubectl create token headlamp --namespace headlamp
```

Copy the token and paste it into the appropriate field in Headlamp.  
You can now inspect the entire cluster through Headlamp.  
Familiarize yourself with Headlamp and the already installed cluster components.

> **Tip:** Headlamp is also available as [desktop app](https://headlamp.dev/docs/latest/installation/desktop/), which simply uses your local kubeconfig.
> Other popular alternatives are the terminal UI [k9s](https://k9scli.io/) and the desktop app [Freelens](https://freelens.app/).

# Install the Linkerd CLI

Follow the [instructions](https://linkerd.io/docs/getting-started/#step-1-install-the-cli) to install the CLI:
```shell
$ export LINKERD2_VERSION=edge-26.11.2
$ curl --proto '=https' --tlsv1.2 -sSfL https://run.linkerd.io/install-edge | sh
```

The open source project Linkerd only publishes so-called *edge releases*. Stable releases are provided by vendors such as Buoyant.
Edge releases are tagged as `edge-<year>.<month>.<number>`.

Add the CLI to your path for easier usage:
```shell
$ export PATH=$HOME/.linkerd2/bin:$PATH
```

If you want to work with Windows & PowerShell, you can download the CLI as an .exe [here](https://github.com/linkerd/linkerd2/releases/tag/edge-26.11.2).

Check if the CLI is installed correctly:
```shell
$ linkerd version
```

You should see the CLI version. The `Server version` will show `unavailable` since Linkerd is not yet installed in the cluster.

# Validate Setup

Validate your setup with:
```shell
$ linkerd check --pre
```

You should see no errors.

# Install Linkerd in the Cluster

First, install the Custom Resource Definitions with the following command:
```shell
$ linkerd install --crds | kubectl apply -f -
```

Install the Linkerd control plane with the following command:
```shell
$ linkerd install | kubectl apply -f -
```

Monitor the installation in Headlamp. Identify which pods make up the control plane.

Check the setup with the following command:
```shell
$ linkerd check
```

You should see no errors after successful installation.

# Linkerd Observability
Linkerd includes an optional observability stack called `Viz`, which makes it much easier for users to see how Linkerd operates in the cluster.  
Install the stack with:
```shell
$ linkerd viz install | kubectl apply -f -
```

Check if everything was installed correctly with:
```shell
$ linkerd check
```

Open the `Viz` dashboard in the browser and get an overview with:
```shell
$ linkerd viz dashboard &
```
You will notice that only the Linkerd control plane and Viz extension pods are currently meshed.

# Install the Demo Application
We will install the Linkerd demo application, Emojivoto, for testing purposes. It allows you to vote for emojis.  
The application can be installed with the following command:
```shell
$ curl --proto '=https' --tlsv1.2 -sSfL https://run.linkerd.io/emojivoto.yml \
  | kubectl apply -f -
```

The application offers a web frontend accessible in the browser through port-forwarding:
```shell
$ kubectl -n emojivoto port-forward svc/web-svc 8080:80
```
The application is now accessible in the browser at [localhost:8080](http://localhost:8080/).  
Familiarize yourself with the application. Occasionally, you may notice errors in the application.  
These are intentional.

You should be able to see the application in the `Viz` dashboard. However, it is not yet meshed, so there are no metrics available.

# Meshing the Demo Application
Linkerd meshes pods when the annotation `linkerd.io/inject: enabled` is present on the pod template or namespace.  
You can mesh Emojivoto with the following command:
```shell
$ kubectl get -n emojivoto deploy -o yaml \
  | linkerd inject - \
  | kubectl apply -f -
```

Compare the output of `kubectl get -n emojivoto deploy -o yaml` with the output of `kubectl get -n emojivoto deploy -o yaml | linkerd inject -`. What did `linkerd inject` change?

The application pods should automatically restart, now with two containers each: the application and the `linkerd-proxy` sidecar.  
Since the pods were replaced, you have to restart the port-forward from the previous section.  
Check the data plane with:
```shell
$ linkerd -n emojivoto check --proxy
```

The application should then appear as meshed in the `Viz` dashboard.  
You can now also see metrics regarding the application's traffic, e.g. the "golden metrics" success rate, requests per second and latency:
```shell
$ linkerd viz -n emojivoto stat deploy
```

By meshing the pods, you have also automatically enabled Linkerd's mTLS encryption for the application.  
All traffic between meshed pods is encrypted end-to-end transparently, without modifications to the application itself.
You can check this with:
```shell
$ linkerd viz -n emojivoto edges deploy
```

# Authorization Policies
Authorization policies can secure service-to-service communication.  
Rules are defined, which are implemented in the sidecar proxy.  
This feature works based on pod identity (which is derived from the Kubernetes service account) and, again, does not require any modifications to the application itself.

Linkerd offers multiple [custom resources](https://linkerd.io/docs/reference/authorization-policy/) for configuring authorization policies.  
First, create a `Server`, which describes a port of a set of pods, in this case the gRPC port of the voting backend:
```yaml
apiVersion: policy.linkerd.io/v1beta3
kind: Server
metadata:
  namespace: emojivoto
  name: voting-grpc
  labels:
    app: voting-svc
spec:
  podSelector:
    matchLabels:
      app: voting-svc
  port: grpc
  proxyProtocol: gRPC
  accessPolicy: deny
```

```shell
$ kubectl apply -f code/linkerd/voting-server.yaml
```

The `Server` covers the port named `grpc` of the voting service.  
Check what effect it has. Does Emojivoto still work as expected? Try to vote for an emoji.

You can also check the status of authorization through the CLI, for example, with:
```shell
$ linkerd viz authz -n emojivoto deploy/voting
```

You probably noticed that the voting backend is no longer accessible.  
Once a `Server` exists, all traffic to its port is denied unless it is explicitly allowed (`accessPolicy: deny`).  
To allow clients, create a `MeshTLSAuthentication`, which describes the clients by their identity, and an `AuthorizationPolicy`, which grants these clients access to the `Server`:
```yaml
apiVersion: policy.linkerd.io/v1alpha1
kind: MeshTLSAuthentication
metadata:
  namespace: emojivoto
  name: web-identity
spec:
  identityRefs:
    - kind: ServiceAccount
      name: web
---
apiVersion: policy.linkerd.io/v1alpha1
kind: AuthorizationPolicy
metadata:
  namespace: emojivoto
  name: voting-grpc-allow-web
spec:
  targetRef:
    group: policy.linkerd.io
    kind: Server
    name: voting-grpc
  # The voting service only allows requests from the web service.
  requiredAuthenticationRefs:
    - group: policy.linkerd.io
      kind: MeshTLSAuthentication
      name: web-identity
```

```shell
$ kubectl apply -f code/linkerd/voting-authorization-policy.yaml
```

Requests from the `web` service to the `voting` backend should now work again.  
Check again with `linkerd viz authz -n emojivoto deploy/voting`.  
Check if you can successfully call the service without the mesh, for example, with:

```shell
$ kubectl run grpcurl --rm -it --image=fullstorydev/grpcurl:v1.9.3 --restart=Never -- -plaintext voting-svc.emojivoto:8080 emojivoto.v1.VotingService/VoteDog
```

Of course, this should not work and fail with `PermissionDenied`. Calls from outside the mesh are not possible. 
Further, the authorization policy you created specifically allows access only for the web service.

# Mesh all the things
Mesh the Traefik gateway and Headlamp.  
Check with `linkerd viz edges` that the traffic between Traefik and Headlamp is secured with mTLS.  
Secure the communication with authorization policies: Headlamp should only be reachable through the gateway.

Hints:
- Instead of using `linkerd inject`, you can also annotate a namespace with `linkerd.io/inject=enabled` and restart its deployments.
- The service account of the gateway is `traefik` in the namespace `traefik`. A `MeshTLSAuthentication` can reference service accounts in other namespaces.
- You can test access from outside the mesh with a pod such as `kubectl run curl --rm -it --image=curlimages/curl:8.17.0 --restart=Never -- -s http://headlamp.headlamp/config`.
- Linkerd automatically authorizes the paths used by Kubernetes for liveness and readiness probes. Have a look at the probes of the Headlamp deployment to understand which path is still reachable.

# Explore Linkerd (optional)
Linkerd has much more to offer.  
Explore more features of the service mesh.  
A good starting point would be the following:
- https://linkerd.io/docs/features/retries-and-timeouts/
- https://linkerd.io/docs/reference/circuit-breaking/
- https://linkerd.io/docs/features/request-routing/
- https://linkerd.io/docs/tasks/distributed-tracing/

# Clean up
Delete the cluster when you are done:
```shell
$ kind delete cluster --name cc-2026
```
