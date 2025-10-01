# Practice Session: Setup

The goal of today's session is to install all the required software components for the upcoming practice sessions. 

## Installation 

1. Git ([Installation](https://git-scm.com/downloads))
2. A Java 21 JDK (z.B. [Azul Zulu JDK](https://www.azul.com/downloads/?package=jdk).
3. A Java IDE of your choice, z.B. [IntelliJ Community Edition](https://www.jetbrains.com/de-de/idea/download/)
4. Docker ([Windows](https://docs.docker.com/docker-for-windows/install/), [Mac](https://docs.docker.com/docker-for-mac/install/), [Linux](https://docs.docker.com/engine/install/))
5. Kind (https://kind.sigs.k8s.io/docs/user/quick-start#installation)
6. Tilt (https://docs.tilt.dev)
7. Kubectl (https://kubernetes.io/docs/tasks/tools/)

## Test the setup

1. Open the terminal/console in a directory of your choice and enter `git clone https://github.com/qaware/cloud-computing-th-rosenheim.git`
2. Open the directory [jdk-test-1](jdk-test-1/) in the terminal/console and enter `mvnw clean install` (`./mvnw clean install` for Mac and Linux)
3. Open the directory [jdk-test-2](jdk-test-2/) in the terminal/console and enter `gradlew clean build` (`./gradlew clean build` for Mac and Linux)
4. Import [jdk-test-1/pom.xml](jdk-test-1/pom.xml) in IntelliJ and start the `main` method
5. Import [jdk-test-2/build.gradle](jdk-test-2/build.gradle) in IntelliJ and start the `main` method
6. Test your [Docker-Setup](https://docs.docker.com/get-started/#test-docker-version) by following the instructions after opening the link in the browser

## Testing the local Kubernetes Cluster
> **Note**
> The following steps require a functional Docker installation!

Open a console and navigate to the folder [kubernetes-cluster-setup](./kubernetes-cluster-setup).

Create a local Kind cluster with the command:
```shell
$ kind create cluster --name cc-2025 --config kind-cluster-config.yaml
```

This may take several minutes the first time. 
Check if the cluster was successfully created with the following command: 
```shell
$ kind get clusters
# expected output --> cc-2025
```

You should see a cluster named `cc-2025`. 
Check if three containers have started and are running without restarts using the following command:
```shell
$ docker ps
```

The output should look something like this:
```
CONTAINER ID   IMAGE                  COMMAND                  CREATED          STATUS          PORTS                                             NAMES
ffc91bee66e0   kindest/node:v1.34.0   "/usr/local/bin/entr…"   45 seconds ago   Up 42 seconds                                                     cc-2025-worker2
bb8e1ac8987d   kindest/node:v1.34.0   "/usr/local/bin/entr…"   45 seconds ago   Up 42 seconds   0.0.0.0:9999->80/tcp, 127.0.0.1:52492->6443/tcp   cc-2025-control-plane
edc7c37d988d   kindest/node:v1.34.0   "/usr/local/bin/entr…"   45 seconds ago   Up 42 seconds                                                     cc-2025-worker
```

Now deploy the test workloads in the cluster with the following command:
```shell
$ tilt up
```
Follow the instructions on the screen and open the Tilt UI in your browser. 
All workloads should start successfully and reach a stable state after a short time.

Finally, we want to ensure that we can communicate with the cluster as expected. 
To do this, open the URL `localhost:9999/foo/hostname` in your browser. 
You should receive `foo-app` as a response. 
Now open the URL `localhost:9999/bar/hostname` in your browser. 
You should receive `bar-app` as a response.

You can now shut down the cluster. To do this, run the following command:
```shell
$ kind delete cluster --name cc-2025
```
