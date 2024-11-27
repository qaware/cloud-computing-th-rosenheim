# Exercise: Service Meshes

In this exercise, you will gain experience with service meshes and some of the provided options.  
We will use the service mesh [LinkerD](https://linkerd.io) in this exercise.

# Cluster Setup
For this exercise, use a fresh Kind cluster with the provided configuration in [kind-cluster-config.yaml](code/clusterconfig/kind-cluster-config.yaml).  
Start the cluster with:
```shell
$ kind create cluster --name cc-2024 --config kind-cluster-config.yaml
```

# Install the Kubernetes Dashboard
This step is not strictly necessary but will help you gain a better understanding.

**Objective**: After this section, you will have installed the Nginx Ingress Controller and can open the Kubernetes Dashboard locally in the browser.

## Install Nginx Ingress Controller

```shell
$ cd code
$ kubectl apply -f nginx-ingress-controller.yaml
```
Depending on your network connection, it may take several minutes for the controller to start successfully.

## Install the Dashboard (optional)

This step may help you to get a better understanding, what's happening inside kubernetes when installing a service mesh. 

Install [helm](https://helm.sh/) if not yet installed. 
Helm is basically a package manager for kubernetes components. 

Execute the following commands in sequence:
```shell
$ cd code/dashboard
# Add kubernetes-dashboard repository
$ helm repo add kubernetes-dashboard https://kubernetes.github.io/dashboard/
# Deploy a Helm Release named "kubernetes-dashboard" using the kubernetes-dashboard chart
$ helm upgrade --install kubernetes-dashboard kubernetes-dashboard/kubernetes-dashboard --create-namespace --namespace kubernetes-dashboard
# Creates a service account named admin-user
$ kubectl apply -f admin-user-serviceaccount.yaml
# Creates a role binding for the service account created.
# This assigns permissions to the service account. In this case: cluster admin
$ kubectl apply -f admin-user-cluster-role-binding.yaml
# Creates an ingress for the dashboard.
# This step makes the dashboard accessible through the nginx ingress controller.
$ kubectl apply -f dashboard-ingress.yaml
```

If you have executed all commands successfully, you should be able to access the dashboard in the browser at `localhost:9999/`.  
The dashboard requires a token for authentication.  
You can generate an admin token for the created service account `admin-user` with the following command:
```shell
$ kubectl -n kubernetes-dashboard create token admin-user
```

Copy the token and paste it into the appropriate field in the dashboard.  
You can now inspect the entire cluster through the dashboard.  
Familiarize yourself with the dashboard and the already installed cluster components.

# Install LinkerD CLI

Follow the [instructions](https://linkerd.io/2.14/getting-started/#step-1-install-the-cli) to install the CLI.  
We recommend using Mac or Linux. If you are on Windows, consider using WSL.  
If you want to work with Windows & PowerShell, you must download the CLI as an .exe [here](https://github.com/linkerd/linkerd2/releases/tag/stable-2.14.4).

It is recommended to add the CLI to your path for easier usage.

Check if the CLI is installed correctly:
```shell
$ linkerd version
```

You should see the CLI version. The `Server version` will likely show an error since Linkerd is not yet installed in the cluster.

# Validate Setup

Validate your setup with:
```shell
$ linkerd check --pre
```

You should see no errors. 
However, due way linkerd changed how releases work, you may see similar warnings, such as `unsupported version channel: stable-2.14.10.`

# Install LinkerD in the Cluster

First, install the Custom Resource Definitions with the following command:
```shell
$ linkerd install --crds | kubectl apply -f -
```

Install the LinkerD control plane with the following command:
```shell
$ linkerd install | kubectl apply -f -
```

Monitor the installation in the dashboard. Identify which pods make up the control plane.

Check the setup with the following command:
```shell
$ linkerd check
```

You should see no errors after successful installation.

# LinkerD Observability
LinkerD includes an optional observability stack called `Viz`, which makes it much easier for users to see how LinkerD operates in the cluster.  
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
You will notice that only the LinkerD control plane and Viz extension pods are currently meshed.

# Install the Demo Application
We will install the LinkerD demo application, Emojivoto, for testing purposes. It allows you to rate emojis.  
The application can be installed with the following command:
```shell
$ curl --proto '=https' --tlsv1.2 -sSfL https://run.linkerd.io/emojivoto.yml \
  | kubectl apply -f -
```

The application offers a web frontend accessible in the browser through port-forwarding:
```shell
$ kubectl -n emojivoto port-forward svc/web-svc 8080:80
```
The application is now accessible in the browser at `localhost:8080/`.  
Familiarize yourself with the application. Occasionally, you may notice errors in the application.  
These are intentional.

You should be able to see the application in the `Viz` dashboard. However, it is not yet meshed, so there are no metrics available.

# Meshing the Demo Application
LinkerD meshes pods when certain annotations are present on the deployment or namespace.  
You can mesh Emojivoto with the following command:
```shell
kubectl get -n emojivoto deploy -o yaml \
  | linkerd inject - \
  | kubectl apply -f -
```

The application pods should automatically restart.  
The application should then appear as meshed in the `Viz` dashboard.  
You can now also see metrics regarding the application's traffic.

By meshing the pods, you have also automatically enabled LinkerD's mTLS encryption for the application.  
All traffic between meshed pods is encrypted end-to-end transparently, without modifications to the application itself.

# Authorization Policies
Authorization policies can secure service-to-service communication.  
Rules are defined, which are implemented in the sidecar proxy.  
This feature works based on pod identity and, again, does not require any modifications to the application itself.

LinkerD offers multiple custom resources for configuring authorization policies.  
First, create a policy for a backend or server.  
Create the following authorization policy:
```yaml
apiVersion: policy.linkerd.io/v1beta1
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
```

The policy covers the port named `grpc` of the voting service.  
Check what effect the policy has. Does Emojivoto still work as expected?

You can also check the status of authorization through the CLI, for example, with:
```shell
$ linkerd viz authz -n emojivoto deploy/voting
```

You probably noticed that the voting backend is no longer accessible.  
In the current configuration, allowed clients must be explicitly specified.  
To do this, create a server authorization:
```yaml
apiVersion: policy.linkerd.io/v1beta1
kind: ServerAuthorization
metadata:
  namespace: emojivoto
  name: voting-grpc
  labels:
    app.kubernetes.io/part-of: emojivoto
    app.kubernetes.io/name: voting
    app.kubernetes.io/version: v11
spec:
  server:
    name: voting-grpc
  # The voting service only allows requests from the web service.
  client:
    meshTLS:
      serviceAccounts:
        - name: web
```

Requests from the `web` service to the `voting` backend should now work again.  
Check if you can successfully call the service without the mesh, for example, with:

```shell
$ kubectl run grpcurl --rm -it --image=networld/grpcurl --restart=Never --command -- ./grpcurl -plaintext voting-svc.emojivoto:8080 emojivoto.v1.VotingService/VoteDog
```

Of course, this should not work. Calls from outside the mesh are not possible. 
Further, the authorization policies you created specifically allow access only for the web service.

# Mesh all the things
Mesh the Nginx ingress controller and the Kubernetes dashboard.  
Secure the communication with authorization policies.

# Explore LinkerD (optional)
LinkerD has much more to offer.  
Explore more features of the service mesh.  
A good starting point would be the following:
- https://linkerd.io/2.14/tasks/configuring-retries/
- https://linkerd.io/2.14/tasks/circuit-breakers/
- https://linkerd.io/2.14/tasks/distributed-tracing/