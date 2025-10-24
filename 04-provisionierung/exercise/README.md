# Übung: Provisioning with Ansible and Docker Compose

## Exercise 1: Provisioning with Docker and Docker Compose

The aim of this exercise is to gain practical experience with Docker and Docker Compose.

In this exercise, we want to:
- use a ready-made Docker image
- build a Docker image
- start an image via Docker Compose

### Step 1: Use the Docker image and start the container

Nginx is an open-source reverse proxy.
https://hub.docker.com/_/nginx documents how the official Docker image
can be used.

Study the documentation and start an Nginx container via the console.

Make sure that port '80' is also exposed on your host as port 8080.

<details>
<summary>Hint if you get stuck:</summary>

```
docker run -d -p 8080:80 nginx
```
</details>

Then enter localhost:8080 in your browser to test it.

### Step 2: Write a Dockerfile and create your own index.html
In this step, we no longer want to use the finished image, but instead build our own
customized image.

First create an index.html. This page should be delivered by our web server when it is accessed.
Use a simple HTML that outputs a “Hello World” greeting.

Create a Dockerfile.
Follow these steps:
Use `nginx:alpine` as the base image.
Copy your `index.html` to `/usr/share/nginx/html/` using the Docker command `COPY`.
Expose port 80.
Start nginx with the command 'nginx'. Be sure to include '-g “daemon off;”' when starting it, so that Nginx is not closed immediately after the container starts, but runs in the foreground.

<details>
<summary>Note if you get stuck:</summary>

```
FROM nginx:alpine
COPY index.html /usr/share/nginx/html
EXPOSE 80
CMD nginx -g 'daemon off;'
```
</details>

### Step 3: Build the Docker image

Build the Docker image named `cc-nginx` and tagged as `v1`.

<details>
<summary>Note if you get stuck:</summary>

```docker build . -t cc-nginx:v1```
</details>

Then use 'docker images' to check that the new Docker image is in your local registry.

### Step 4: Start container

Start the container.
Map port 8080 on your host to container port 80.
Ensure that the container from step 1 is shut down to avoid port conflicts.

<details>
<summary>Note if you get stuck:</summary>

```docker run -it -p 8080:80 cc-nginx:v1```
</details>

Access the started nginx container at localhost:8080.

### Step 5: Start container via Docker Compose

We now use our Docker image and start it via Docker Compose.

To do this, create a docker compose.yaml file that starts an nginx instance and:

- has the service name “cc-nginx”
- builds the image based on the Dockerfile created in step 1
- exports port 80 and makes it accessible on port 8080 on your host
     
<details>
<summary>If you get stuck, you can use the following code block:</summary>

```
version: '3.8'
services:
  cc-nginx:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:80"
```
</details>

### Bonus/Optional:

Try out some more Docker and Docker Compose commands.
Open a shell in the running container
Edit the index.html in the container
Look at the nginx logs
...


## Exercise 2: Provisioning with Ansible

The goal of this exercise is to gain practical experience with Ansible and to get to know Docker and Docker Compose
even better.

To keep the local setup as simple as possible, we will use Docker Compose to start both the Ansible Control Node and
the machines we want to provision locally.

We want to:
- Start the Ansible control node with Docker Compose
- Start 3 managed nodes with Docker Compose
- Create a playbook that installs, configures and starts an Apache HTTP server on all 3 managed nodes
- Run the playbook and access the started Apache HTTP servers for testing purposes

### Note:
If you get stuck, you can refer to the sample solution.

You can also use the following references:
- Docker Compose Syntax: https://docs.docker.com/compose/compose-file/
- Ansible Documentation: https://docs.ansible.com/ansible/latest/index.html 

### Step 1: Build an image for managed nodes

Create a Dockerfile for the machines/managed nodes to be provisioned.
These should include Ubuntu version 25.10 and allow SSH connections from outside:
```
  apt-get update && apt-get install -y openssh-server
  mkdir /var/run/sshd
  echo 'root:verysecretpassword' | chpasswd
  sed -i 's/#*PermitRootLogin prohibit-password/PermitRootLogin yes/g' /etc/ssh/sshd_config
  sed -i 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' /etc/pam.d/sshd
  echo "export VISIBLE=now" >> /etc/prof
```

The ssh server is then started with the following command:
```
  /usr/sbin/sshd -D
```

#### Bonus/Optional:

Alternatively, you can create a Docker image that allows external SSH connections:
- Choose a Ubuntu base image.
- Make sure that an SSH connection to the container can be established (via SSH daemon),
  and store the credentials username=“root” and password=“verysecretpassword” for this purpose.

### Step 2: Start managed node and Ansible control node together using Docker Compose

Create a Docker Compose file (file named “docker-compose.yml” alongside this readme) that
- starts a managed node and in doing so:
  - has the service name “managed-node”
  - builds the image based on the Dockerfile created in step 1
  - exports port 80

     <details>
     <summary>If you get stuck, you can use the following code block:</summary>
     
     ```
  managed-node:
    build:
      context: .
      dockerfile: Dockerfile_Managed_Node
    ports:
      - "80"
     ```
     </details>
- starts an Ansible Control Node and in doing so:
  - has the service name “ansible-node”
  - uses the finished image “willhallonline/ansible:2.18-alpine-3.22”. This provides Ansible version 2.18 with
    Python3. (see https://hub.docker.com/r/willhallonline/ansible)
  - has a memory limit of 100 MB
  - is only started when the managed node is running

     <details>
     <summary>If you get stuck, you can use the following code block:</summary>
     
     ```
  ansible-node:
    image: "willhallonline/ansible:2.18-alpine-3.22"
    networks:
      - cloudcomputing
    depends_on:
      - managed-node
    ```
    </details>
In the Docker Compose file, specify a network of type “bridge” and make sure that the
Managed Node and the Ansible Control Node run in the same network. Remember to specify this in the Docker Compose file
for each of the services using “networks”.

### Step 3: Test the setup
Start the Managed Node via
```
docker compose up --build -d
```
Start a shell on the Ansible control node via
```
docker compose run ansible-node /bin/sh
``` 

Establish an SSH connection to the managed node using the following command:
```
ssh managed-node
```
Enter the password 'verysecretpassword' when prompted.
You can terminate the SSH connection with ```exit.

You can establish an SSH connection both via the service name 'managed-node' and with the prefix 'uebung-ansible' (the name
of the folder) and suffix '1'. Verify this with 
```
ssh uebung-ansible-managed-node-1
```

### Step 4: Getting started with Ansible

Configure the machines that you want to provision with Ansible using the hosts file.
To do this, create an “ansible” folder and a “hosts” file inside it.

Create the group “server_hosts”, enter the managed node in it, and configure Ansible:
- Specify python3 as the Python interpreter
  - Enter the username and password for the SSH connection and add the ssh argument '-o StrictHostKeyChecking=no'

       <details>
       <summary>If you get stuck, you can use the following code block:</summary>

      ```
        [server_hosts]
        uebung-ansible-managed-node-1

        [server_hosts:vars]
        ansible_python_interpreter=/usr/bin/python3
        ansible_ssh_user=root
        ansible_ssh_pass=verysecretpassword
        ansible_ssh_common_args='-o StrictHostKeyChecking=no'
        ```

      </details>

Mount this file via Docker Compose for the ansible-node service at /etc/ansible/hosts:

```
    volumes:
    [...]
    - "./ansible/hosts:/etc/ansible/hosts"
```

Restart the Ansible node via
```
docker compose run ansible-node /bin/sh
``` 

Run the following commands in it
- Output version: ansible --version
- Ping all managed nodes: ansible all -m ping
- Run remote command on all managed nodes: `ansible all -m command -a 'uptime'`

### Step 6: Install and configure the Apache HTTP server using an Ansible playbook

Create the folder 'playbooks' and inside it a file named 'install-apache.yml'.
Familiarize yourself with the Ansible syntax at https://docs.ansible.com/ansible/latest/index.html.

Enter the hosts 'server_hosts' and the remote user 'root'.

Mount the playbook via Docker Compose into the Ansible Control Node under '/root/playbooks'.

     <details>
     <summary>In case you get stuck you can use the following code block:</summary>

      ```
          volumes:
          [...]
          - "./playbooks:/root/playbooks"
      ```

     </details>

In the Playbook, perform the following tasks:

- Test whether you can establish a connection to the managed node. To do this, use the Ansible module 'ping'
  (https://docs.ansible.com/ansible/latest/modules/ping_module.html#stq=copy%20module&stp=1).
- Use the Ansible module 'apt' to install the 'latest' version of Apache2 (https://docs.ansible.com/ansible/latest/modules/apt_module.html).
- Create your own index.html that you want Apache to deliver and copy it to the managed node under '/var/www/html/index.html'.
  Use the Ansible module 'copy' (https://docs.ansible.com/ansible/latest/modules/copy_module.html) for this.
- Use the Ansible module 'service' to ensure that the Apache HTTP server has been started (https://docs.ansible.com/ansible/latest/modules/service_module.html).

Run the playbook on the Ansible control node:

```
ansible-playbook /root/playbooks/install-apache.yml
```

### Step 6: Starting the web server

Find out via 

```
docker ps
```

under which port the started web server can be accessed on your host.
Which port forwards requests to the exposed port '80' of the container?

In your browser, enter localhost:<port> for the specific port and verify that your index.html
is displayed.

### Step 7: Scale the managed nodes

Scale the managed nodes to 3.
Use the docker compose command 'up' with the '--scale' option (see https://docs.docker.com/compose/reference/up/) or add the scale configuration to the docker-compose.yml

Modify the 'hosts' file so that all 3 managed nodes can be provisioned and execute the
provisioning.

### Troubleshooting
Make sure that you allow access to the mounted files via Docker.


## Exercise 3: Packer (Optional)

### Step 1: Install packer
Install packer by following the instructions on https://learn.hashicorp.com/tutorials/packer/getting-started-install
.

### Step 2: Get to know Packer
Familiarize yourself with Packer by looking at the examples at https://learn.hashicorp.com/tutorials/packer/getting-started-build-image?in=packer/getting-started
.

Optional: build one of the examples locally with Packer.

### Step 3: Build a Docker image with Packer
Read the documentation on building Docker images with Packer: https://www.packer.io/docs/builders/docker

Create a Packer template.
Use Packer's Docker Builder to build an Nginx image with
its own welcome page.

Use “nginx:1.29-alpine” as the base image.

Expose port 80 in the image and execute the CMD “nginx -g daemon off;”
to start Nginx.

Since we are using an Alpine image in which `bash` is not installed by default,
use the following run_command
from Packer so that `/bin/sh` is used instead of
'/bin/bash` when the container is started later:
...
```"run_command": [ "-d", "-t", "-i", "{{.Image}}", "/bin/sh" ] ```

Use Packer's file provisioner to copy a locally created index.html
to /usr/share/nginx/html/ in the image.

Use Packer's post-processor “docker-tag” to give the image the.
Name “packer-nginx” and the tag “1.0”.

Execute
```packer build <Your Template>``` .

Use ```docker images``` to check whether the Docker image is available in your local registry.

Then start the container with docker.
Map the container port 80 to the host port 8080.

<details>
<summary>Note if you get stuck:</summary>

```
docker run -d -p 8080:80 packer-nginx:1.0
```
</details>

In the browser, enter ```localhost:8080``` and verify
that your welcome page is displayed.

### Bonus/Optional 1:
Use an Alpine or Centos image instead of the Nginx image.
Install Nginx using the shell provisioner.

### Bonus/Optional 2: Run Ansible Provisioning with Packer

Use Ansible for provisioning with Packer.
Use the Playbook from Exercise 2 and run it with the
Ansible Provisioner from Packer.

Use the Packer documentation and research examples.
