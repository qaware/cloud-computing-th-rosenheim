# Exercise: PaaS on AWS with Amazon ECS Express Mode

## Goal

In the IaaS exercise you built the VPC, load balancer, security groups and auto scaling group yourself, piece by piece.
In this exercise you will use **Amazon ECS Express Mode**, a PaaS offering on AWS: you provide a container image and a few settings, and AWS provisions and operates everything else.

You will:

1. Set up the AWS CLI on your local machine.
2. Create a VPC for your application.
3. Create a private container registry in Amazon ECR.
4. Build the provided container image locally and push it to ECR.
5. Deploy the image with ECS Express Mode and see your app launch.
6. Roll out a new version of your app.
7. Clean up.

## Prerequisites

- A local [Docker](https://docs.docker.com/get-docker/) installation.
- The [AWS CLI v2](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html) installed on your local machine.
  Check with `aws --version`, which should print `aws-cli/2.x.x ...`.
  If you use your own AWS account, you need version `2.32.0` or newer.
- An AWS account. Either:
  - **Course account**: access via `https://cc-th-rosenheim.awsapps.com/start`, provided by your instructor, or
  - **Own account**: your personal [AWS account](https://aws.amazon.com/free/).

> [!WARNING]
> **Own account only:** this exercise is **not** completely covered by the AWS Free Tier.
> The load balancer and the Fargate tasks cost a few cents per hour while they are running.
> Follow the clean up in Task 8 when you are done, and consider setting up a [budget alert](https://docs.aws.amazon.com/cost-management/latest/userguide/budgets-create.html) beforehand.

**Notes**:

- Make sure the AWS Region is set to **eu-central-1 (Frankfurt)** (top right of the console).
- Choose a **unique name**, e.g. `lbuchner` for `Lukas Buchner`, and use it for every resource you create.
  In the course account all participants share the same account, so without unique names you and your classmates will not find your resources.
  Avoid special characters except `-`, avoid umlauts, and use lowercase only (ECR repository names must be lowercase).
- In the following, `<NAME>` refers to your unique name.

## Task 1: Set up the AWS CLI

In this task you configure an AWS CLI profile called `student`. All later commands use this profile.
Follow **either** option A or option B, depending on which account you use.

### Option A: Course account

Configure the AWS CLI with the course SSO login:

``` shell
aws configure sso
```

Enter session name `my-sso`, SSO start URL `https://cc-th-rosenheim.awsapps.com/start`, SSO region `eu-central-1`, default client region `eu-central-1`, output format `json` and profile name `student`.
Leave the other values unchanged. A browser window will open in which you confirm the login.

> [!TIP]
> SSO sessions expire after some hours. If a command fails with an expired token, run `aws sso login --profile student`.

### Option B: Own account

Log in with the same credentials you use for the AWS management console:

``` shell
aws login --profile student
```

When asked for a region, enter `eu-central-1`. A browser window will open in which you sign in and confirm the login.
The CLI then receives temporary credentials, so you don't need to create or store any access keys.

> [!NOTE]
> Ideally you don't use the root user (the email address you signed up with) for daily work, but an IAM user or IAM Identity Center user with administrator permissions.
> For this exercise, any identity with administrator permissions works.

<details>
<summary>Alternative: access keys (if your AWS CLI is older than 2.32.0)</summary>

> Create an IAM user with the `AdministratorAccess` policy in the IAM console, open it, and under _Security credentials_ choose _Create access key_ (use case _Command Line Interface (CLI)_).
> Then run:
>
> ``` shell
> aws configure --profile student
> ```
>
> Enter the access key id, the secret access key, region `eu-central-1` and output format `json`.
> Access keys are long-lived credentials: never commit them to Git, and delete them once you are done with the exercise.

</details>

### Use the profile

Make the profile your default for the current shell:

``` shell
# macOS / Linux
export AWS_PROFILE=student
```

``` powershell
# Windows PowerShell
$env:AWS_PROFILE = "student"
```

Check your configuration:

``` shell
aws sts get-caller-identity
```

You should see a JSON response containing the `Account` id. Note it down, you will need it later.

## Task 2: Create a VPC

Express Mode places a public load balancer and your containers into a VPC.
It needs **at least two public subnets in two different availability zones**.

In the IaaS exercise you created a VPC, subnets, internet gateway and route table by hand.
This time we use the VPC wizard, which creates all of that in one go.

1. Open the _VPC_ service and choose _Your VPCs_ > _Create VPC_.
2. Choose **VPC and more**.
3. Configure:
   - _Name tag auto-generation_: `<NAME>`
   - _IPv4 CIDR block_: `10.0.0.0/16`
   - _Number of Availability Zones (AZs)_: `2`
   - _Number of public subnets_: `2`
   - _Number of private subnets_: `0`
   - _NAT gateways_: `None`
   - _VPC endpoints_: `None`
   - Leave _Enable DNS hostnames_ and _Enable DNS resolution_ checked.
4. Check the _Preview_ on the right: you should see your VPC, two public subnets, a route table and an internet gateway.
5. Choose _Create VPC_.

Questions:

- Which resources did the wizard create that you created manually in the IaaS exercise?
- What makes a subnet a _public_ subnet?

<details>
<summary>Solution</summary>

> The wizard created the VPC, two subnets in `eu-central-1a` and `eu-central-1b`, an internet gateway attached to the VPC, and a route table with a route `0.0.0.0/0` to the internet gateway, associated with both subnets.
>
> A subnet is public because its route table sends internet traffic (`0.0.0.0/0`) to an internet gateway.
> Without that route, resources in the subnet cannot be reached from or reach the internet directly.

</details>

## Task 3: Create an ECR repository

[Amazon Elastic Container Registry (ECR)](https://aws.amazon.com/ecr/) is a private container registry, comparable to Docker Hub, but inside your AWS account.

1. Open the _Elastic Container Registry_ service and choose _Create repository_ (under _Private registry_ > _Repositories_).
2. Configure:
   - _Repository name_: `<NAME>-paas`
   - _Image tag mutability_: `Mutable`
   - Leave the encryption settings at their defaults.
3. Choose _Create_.
4. Open your repository and note its **URI**. It looks like this:

   ``` text
   <ACCOUNT_ID>.dkr.ecr.eu-central-1.amazonaws.com/<NAME>-paas
   ```

## Task 4: Build and push the container image

The directory [`app`](./app) contains a small web application written in Python, together with a `Dockerfile`.
It listens on port `8080` and has two endpoints:

- `/` shows a page with the app version and the hostname of the container that served the request.
- `/health` returns `OK` and is used as the health check.

1. Optional: try the app locally.

   ``` shell
   cd app
   docker build -t paas-app .
   docker run --rm -p 8080:8080 paas-app
   ```

   Open http://localhost:8080 and stop the container with `CTRL+C`.

2. Set some variables for the following commands (replace the placeholders):

   ``` shell
   export ACCOUNT_ID=<ACCOUNT_ID>
   export REPO=$ACCOUNT_ID.dkr.ecr.eu-central-1.amazonaws.com/<NAME>-paas
   ```

   On Windows PowerShell use `$env:ACCOUNT_ID = "..."` and `$env:REPO = "..."`, and refer to them as `$env:REPO` in the commands below.

3. Log Docker in to your ECR registry. The AWS CLI fetches a temporary password and pipes it to `docker login`:

   ``` shell
   aws ecr get-login-password --region eu-central-1 \
     | docker login --username AWS --password-stdin $ACCOUNT_ID.dkr.ecr.eu-central-1.amazonaws.com
   ```

   You should see `Login Succeeded`.

4. Build the image for the `linux/amd64` platform and tag it with your repository URI:

   ``` shell
   docker build --platform linux/amd64 -t $REPO:v1 .
   ```

   > [!IMPORTANT]
   > If you are using a Mac with Apple Silicon (M1/M2/...), your machine builds `arm64` images by default.
   > ECS Express Mode runs `X86_64` by default, so an `arm64` image would fail to start with `exec format error`.
   > `--platform linux/amd64` makes sure the image matches.

5. Push the image:

   ``` shell
   docker push $REPO:v1
   ```

6. In the ECR console, open your repository and check that the image with tag `v1` is listed.

> [!TIP]
> The ECR console also shows these commands: open your repository and choose _View push commands_.

## Task 5: Deploy with ECS Express Mode

1. Open the _Elastic Container Service_ service and choose **Express mode** in the navigation pane.
2. Under _Image URI_ choose _Browse ECR images_, select your repository `<NAME>-paas` and the image tag `v1`.
3. _Task execution role_: allows ECS to pull your image from ECR and write logs to CloudWatch.
   - **Course account**: select `ecsTaskExecutionRole`.
   - **Own account**: select _Create new role_. If a role `ecsTaskExecutionRole` already exists in your account, you can select it instead.
4. _Infrastructure role_: allows ECS to create the load balancer, security groups, auto scaling etc. on your behalf.
   - **Course account**: select `ecsInfrastructureRoleForExpressServices`.
   - **Own account**: select _Create new role_.

   > [!NOTE]
   > In the course account both roles are already prepared and you don't have permissions to create new ones. Do **not** choose _Create new role_ there.
   > In your own account, roles selected with _Create new role_ are created when you choose _Create_ in step 6.

5. Expand _Additional configurations_ and configure:
   - _Name_: `<NAME>-paas`
   - _Container port_: `8080`
   - _Health check path_: `/health`
   - _Environment variables_: add
     - `APP_VERSION` = `v1`
     - `GREETING` = a greeting of your choice
   - _Compute and Auto scaling_: choose the smallest available _CPU_ and _Memory_ (e.g. `0.25 vCPU` / `0.5 GB`; the defaults of 1 vCPU / 2 GB are far more than this app needs), set _Minimum number of tasks_ to `2` and _Maximum number of tasks_ to `4`.
   - Check **Customize networking configurations**, select **your** VPC and **both** of its public subnets.
6. Choose _Create_.

   > [!NOTE]
   > If you see `Unable to assume the service linked role`, wait a few seconds and choose _Create_ again.
   > This happens especially in a new account, where AWS has to create the required roles first.

7. Watch the _Resources_ tab while Express Mode provisions your service. This takes a few minutes.
8. Once the deployment is complete, open the **Application URL** (`https://<NAME>-paas.ecs.eu-central-1.on.aws`).
   You should see your greeting.
9. Reload the page a few times. What do you notice about the _Served by (hostname)_ field? Why?

<details>
<summary>Solution</summary>

> The hostname changes between two values, because two tasks (containers) are running and the load balancer distributes the requests between them.
> Depending on your browser, connections may be reused, so you might have to reload more often or use `curl` to see both.

</details>

### Explore what was created

Express Mode created a lot of infrastructure for you. Look at the _Resources_ tab of your service and answer:

- Which resources did Express Mode create?
- Compare them with the IaaS exercise. Which did you have to create yourself back then?
- Your app listens on plain HTTP on port `8080`, but the Application URL uses HTTPS. Where does HTTPS come from?
- Where can you find the logs of your app? Look for the line `Starting app v1 on port 8080`.

<details>
<summary>Solution</summary>

> Express Mode created (among others):
>
> - an ECS cluster (`default`) running on Fargate, so there are no EC2 instances to manage
> - a task definition and an ECS service
> - an Application Load Balancer with an HTTPS listener, listener rule and target group
> - security groups for the load balancer and the service
> - an ACM certificate for the `*.ecs.eu-central-1.on.aws` domain
> - auto scaling (scalable target and target tracking policy on CPU utilization)
> - a CloudWatch log group and an alarm used to roll back failed deployments
>
> In the IaaS exercise you created the load balancer, target group, security groups, launch template and auto scaling group yourself, and you had to manage the VM and its operating system.
> Here you only provided a container image, a port and a health check path.
>
> HTTPS is terminated at the load balancer using the ACM certificate. The load balancer then forwards plain HTTP to your containers on port `8080`.
>
> The logs are in CloudWatch Logs. You can reach them via the _Logs_ tab of the ECS service, or via the log group in the CloudWatch console.

</details>

## Task 6: Roll out a new version

Your app is live, now ship an update.

1. Change something visible in [`app/server.py`](./app/server.py), for example the `COLOR` value.
2. Build and push the image with a new tag `v2`:

   ``` shell
   docker build --platform linux/amd64 -t $REPO:v2 .
   docker push $REPO:v2
   ```

3. In the ECS console, open your Express Mode service and choose _Update_.
   - Select the image tag `v2`.
   - Change the environment variable `APP_VERSION` to `v2`.
   - Save the update.
4. Keep reloading your Application URL while the deployment is running.
   Does the app become unavailable at any point? When does `v2` appear?

Alternatively, you can trigger the update from the CLI.
Find the ARN of your service in the console, then run:

``` shell
aws ecs update-express-gateway-service \
  --service-arn <SERVICE_ARN> \
  --primary-container '{"image":"'$REPO':v2","containerPort":8080,"environment":[{"name":"APP_VERSION","value":"v2"},{"name":"GREETING","value":"Hello from v2!"}]}' \
  --monitor-resources
```

Questions:

- Why does pushing a new image to ECR not automatically update the running app?
- Why is it good practice to use a new tag (`v2`) instead of overwriting `v1` or using `latest`?

<details>
<summary>Solution</summary>

> The app stays available. Express Mode uses a canary deployment: new tasks with `v2` are started and must pass the health check before traffic is shifted to them. The old tasks are stopped afterwards.
> If the new version keeps failing, an alarm triggers an automatic rollback.
> For a while you might see `v1` and `v2` alternating.
>
> ECS resolves the image tag to an immutable image digest when a deployment starts. Running tasks keep using that exact image.
> A new image is only used when a new deployment is started, e.g. through an update.
> Unlike Vercel, there is no direct connection to your Git repository here. You would set this up yourself with a CI/CD pipeline (e.g. GitHub Actions) that builds, pushes, and calls `update-express-gateway-service`.
>
> Unique tags make it clear which version is running, and allow you to roll back by simply deploying the old tag again.
> With `latest` you cannot tell which code is behind it.

</details>

## Task 7 (Optional): Break it

What happens if your new version is broken?

1. Build and push a tag `v3`, but in the ECS update set the _Health check path_ to `/does-not-exist`, or change `server.py` so that `/health` returns status `500`.
2. Update your service to `v3` and observe the deployment in the console.

<details>
<summary>Solution</summary>

> The new tasks never become healthy in the load balancer's target group, so no traffic is shifted to them.
> The deployment fails and is rolled back. Your users keep seeing the previous version the whole time.

</details>

## Task 8: Clean up

Express Mode resources cost money while they are running, mainly the load balancer and the Fargate tasks.
Delete everything in this order:

1. **ECS service**: open your Express Mode service and delete it.
   This also removes the resources Express Mode created for it (load balancer, target group, security groups, auto scaling, ...).
   Wait until the deletion is complete.
   Alternatively via CLI:

   ``` shell
   aws ecs delete-express-gateway-service --service-arn <SERVICE_ARN> --monitor-resources
   ```

2. **ECR repository**: delete your repository `<NAME>-paas` including its images.
3. **VPC**: in the VPC console, select your VPC and choose _Actions_ > _Delete VPC_.
   This also deletes its subnets, route table and internet gateway.
   If the deletion fails because of remaining network interfaces or security groups, the service deletion is not finished yet. Wait a few minutes and try again.

**Course account**: do **not** delete the `default` ECS cluster or the IAM roles, other students use them as well.

**Own account**: you can additionally delete
- the `default` ECS cluster in the ECS console (an empty cluster costs nothing, though),
- the IAM roles `ecsTaskExecutionRole` and the infrastructure role created in Task 5,
- the access keys and IAM user, if you used the access key alternative in Task 1.

Finally, run `aws logout --profile student` to clear the CLI login.
