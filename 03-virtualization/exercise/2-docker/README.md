# Exercise: Virtualization with Docker

## Preparation

This tutorial requires Docker to be installed on your local machine.

To verify that your Docker installation works correctly, run:

```bash
docker run hello-world
```

If this command completes successfully, you’re ready to begin.

---

## Part 1 – Basic Docker Usage

1. **Start a container**
   Launch a Docker container based on the *alpine* image and start an interactive shell as the entrypoint process.
   You can find the required base image on [Docker Hub](https://hub.docker.com).

2. **Check your user identity**
   Run `whoami` inside the container to see which user you’re logged in as.
   You’ll notice you are logged in as **root**, which has the highest privileges — but only *within this container’s namespace*.

3. **Initialize the package manager**
   Update the package index using:

   ```bash
   apk update
   ```

4. **Install NGINX and curl**
   Use Alpine’s package manager to install the web server and curl:

   ```bash
   apk add nginx curl
   ```

5. **Start the NGINX web server**
   Launch NGINX with:

   ```bash
   nginx
   ```

   **Note:** If you encounter the error
   `_open() "/run/nginx/nginx.pid" failed_`,
   create the missing directory using:

   ```bash
   mkdir /run/nginx
   ```

6. **Test the web server**
   Use curl to fetch the web page served by NGINX on port 80:

   ```bash
   curl localhost
   ```

   You should see the default NGINX welcome page.

7. **Access from the host**
   Try accessing the same page from your host system.
   You’ll notice that it’s *not accessible* — the container is isolated from the host by default.

8. **Exit the container**
   Leave the container shell by typing `exit`.
   Where are you now?

9. **Inspect Docker containers**
   List all containers managed by Docker:

   ```bash
   docker ps -a
   ```

   Find the container ID of the one you just exited.
   Why doesn’t it appear when you run only `docker ps`?

---

### Bonus

1. **Create a new image**
   Commit your container’s current state to a new image:

   ```bash
   docker commit <CONTAINER ID> cloudcomputing/nginx
   ```

2. **List available images**
   Confirm that your new image was created:

   ```bash
   docker images
   ```

3. **Run the container in background mode**
   Start the new container in detached mode, mapping port 80 of the container to port 80 on the host:

   ```bash
   docker run -d -p 80:80 cloudcomputing/nginx nginx -g "daemon off;"
   ```

   *(The `daemon off` option ensures NGINX runs in the foreground so the container stays alive.)*

4. **Check running containers**

   ```bash
   docker ps
   ```

5. **Access via localhost**
   Use curl or a browser on your host to access the NGINX web page:

   ```bash
   curl localhost
   ```

   Which URL works in your browser?

6. **Inspect and review logs**
   Use:

   ```bash
   docker inspect <CONTAINER ID>
   docker logs <CONTAINER ID>
   ```

   to view container details and logs.

---

## Part 2 – Building with a Dockerfile

In Part 1, we created an image manually and configured NGINX via command-line arguments.
Now we’ll automate this using a Dockerfile.

1. **Choose a base image**
   Use `alpine:3.11` as the base image.
   This automatically pulls the latest bugfix version within that release (e.g., 3.11.6).

2. **Create a Dockerfile**
   Write a `Dockerfile` to build the image automatically.

3. **Use the following Docker instructions**
   Include commands such as:

   ```
   RUN
   EXPOSE
   ENTRYPOINT
   CMD
   ```

4. **Build the image**

   ```bash
   docker build -t cloudcomputing/nginx .
   ```

5. **Run the image**

   ```bash
   docker run -d -p 80:80 cloudcomputing/nginx
   ```

6. **Test functionality**

   ```bash
   curl localhost
   ```

   You should see the NGINX welcome page again.

---

## Part 3 – Serving Custom Content with Volumes

Next, we’ll configure NGINX to serve your own HTML file.

1. Use the official NGINX Docker image:

   ```bash
   docker run -p 80:80 nginx:mainline
   ```

2. To serve your custom demo webpage from the `content` directory on your host, map this directory to the NGINX web root inside the container using a **volume mount**.

   **Hint:**
   Inside the container, NGINX serves files from:

   ```
   /usr/share/nginx/html
   ```

   Adjust your run command accordingly.

---

## Cleanup

Docker can consume a significant amount of disk space over time.
To quickly remove all unused containers, images, and resources, run:

```bash
docker system prune
```

---

## Further Learning

To gain a deeper understanding of Docker concepts beyond this exercise, complete the following **Docker Basics** course on [Killercoda](https://killercoda.com/brian/course/docker-basics):

Work through these scenarios as extended practice:

* Docker commands
* Docker storage
* Docker networking
* Dockerfile creation

---

## References

For additional information, consult these official and community resources:

* [Docker Documentation](https://docs.docker.com)
* [Interactive Docker Tutorial](https://www.docker.com/tryit)
* [Docker Command Cheat Sheet](https://github.com/wsargent/docker-cheat-sheet)
* [Docker CLI and Dockerfile Reference](https://docs.docker.com/reference/)


