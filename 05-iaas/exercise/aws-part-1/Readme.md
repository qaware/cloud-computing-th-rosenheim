# Practice: IaaS with the AWS Management console

## Goal

The goal of this exercise is for you to gain a fundamental understanding of the AWS Cloud and its basic IaaS components. 
Your task is to create a virtual network, an auto scaling group with multiple machines, and a load balancer to deploy a minimal web application. 
Afterwards, you will use the AWS CLI to scale your application and make changes via SSH.

## Prerequisites

In the second part of this exercise, we will use a Docker container for using the CLI. Therefore, you need a local Docker installation. 
Since creating/downloading the container may take some time, please start with the preparation for the second part.

You will need an AWS account to complete the exercise. 
You may get access via your instructor.
If you use awseducate.com, log in on the website, click on AWS Account, and create an [Educate Starter Account](https://www.geeksforgeeks.org/aws-educate-starter-account/).

## Tasks

### Create AWS infrastructure via the UI

In this exercise, you will create a simple IaaS architecture using the AWS Web Console.

**Notes**:

- Follow the instructions; unless stated otherwise, leave other settings unchanged.
- Ensure that the AWS Region is set to eu-central-1 (Frankfurt) (top right of the screen) and the language is set to English (bottom left of the screen). If the language remains in German after logging in, log out and select English below the login screen before logging in again.
- **Important**: if you are sharing an account with other participants: 
Think of a unique name for naming/tags for your resources. 
- Since all participants will use the same account, if your resources are not distinctly named, you and your classmates may have trouble locating them. 
- You could, for instance, use a combination of your real name, such as `lbuchner` if your name is `Lukas Buchner`, or choose any other unique name like anonymousant. Avoid special characters in names except for - and avoid using umlauts.

#### Create a VPC

In this part of the exercise, you will create a virtual network with a single subnet with internet access.

1. Click on _Services_ in the menu bar, select _VPC_ > _Your VPCs_ > _Create VPC_.
   * Give it a unique name that you can use to identify your network, e.g., _lbuchner_.
   * Use the IPv4 address range `10.0.0.0/16`.
   * Click _Create VPC_.  
     After creating the VPC, you will notice that additional standard cloud resources, such as a routing table, have been created.

2.
   * a) Click on _Subnets_ in the sidebar, then select _Create subnet_.
      * Choose the VPC you created as the target for your subnet.
      * Use your unique name again.
      * Select the _Availability Zone_ _eu-central-1a_.
      * Choose an address range smaller than that of your VPC, for example, half: `10.0.0.0/17`.
      * Click _Create subnet_.
      * After creation, select your network, click _Actions_, and choose _Edit subnet settings_, then enable _Enable auto-assign public IPv4 address_.
   * b) Repeat steps in 2a to create a subnet in _eu-central-1b_ with the address range `10.0.128.0/17`.

3. Create internet access by clicking on _Internet gateways_ in the sidebar and then clicking _Create internet gateway_.
   * Use your unique name again.
   * After creating, click _Actions_ in the upper right and select _Attach to VPC_.
   * Choose your VPC and confirm.

4. Now click on _Route tables_ in the sidebar and add another rule to the table for your VPC that routes all traffic (`0.0.0.0/0`) to the internet gateway you created.
   The order of rules is irrelevant; more specific rules are applied first.

#### Create a load balancer

In this part of the exercise, you will create a load balancer to distribute requests across the currently available instances.

1. Go to the _EC2_ service.
   Click on _Security Groups_ in the sidebar, then click _Create security group_.
   * Name the group using the format `loadbalancer-<Your Unique Name>`.
   * For the description, _"Security Group for the Load Balancer."_ is appropriate.
   * Under _VPC_, select your VPC.
   * Add an inbound rule to allow access on port `80`, i.e., HTTP, from `0.0.0.0/0`, meaning any IP.
   * Click _Create security group_.

2. Click on _Load Balancers_ in the sidebar and select _Create Load Balancer_.
   * Choose the type _Application Load Balancer_.
   * Use your unique name again.
   * Under _Network mapping_, select your VPC and your subnets.
   * Under _Security groups_, select only your `loadbalancer-<Your Unique Name>` security group.
   * Under _Listeners and routing_, create a new target group and use your unique name for it.
      * Under _Basic configuration_
         * Select the type `Instances`.
         * Choose the protocol `HTTP`.
         * Select port `8080`.
      * Under _Health checks_
         * Select the protocol `HTTP`.
         * Choose the path `/`.
      * Under _Advanced health check settings_
         * Select the port `Traffic port`.
         * Set the _Healthy threshold_ to `2`.
         * Set the _Unhealthy threshold_ to `2`.
         * Set the _Interval_ to `10 seconds`.
      * There is no need to register _Targets_, as this will be managed by the auto scaling group.
      * Complete the process by clicking _Create target group_.
   * Select the target group you created.
   * Finally, click _Create load balancer_.

#### Create an auto scaling group

In this part of the exercise, you will create an Auto Scaling group, which allows you to easily scale and replace your instances. Additionally, your Auto Scaling group will register your instances with the target group of the load balancer.

1. Create another security group.
   * Name the group using the format `app-<Your Unique Name>`.
   * For the description, _"Security Group for the application servers."_ is appropriate.
   * Under _VPC_, select your VPC.
   * Add an inbound rule to allow access on port `22`, i.e., SSH, from `0.0.0.0/0`, meaning any IP.
   * Add another inbound rule to allow access on port `8080` from the load balancer's security group.
   * Click _Create security group_.

2. Click on _Launch Templates_ in the sidebar, then _Create Launch Template_.
   * Use your unique name again.
   * For the description, "Launch Template for a simple web application." is appropriate.
   * Under _Application and OS Images_, choose an Ubuntu _AMI_, for example, `ami-0084a47cc718c111a`.
   * Under _Instance type_, select `t2.micro`.
   * Under _Network settings_, select your security group for the application.
   * Under _Advanced details_, use the following script as _User Data_:

     ``` shell
     #!/bin/bash
     set -euxo pipefail

     apt-get update
     apt-get install -y busybox cowsay
     source /etc/environment

     {
       echo "<pre>"
       /usr/games/cowsay -f dragon Hello World
       echo "</pre>"
     } >> index.html

     nohup busybox httpd -f index.html -p 8080 &
     ```

   * Finish by clicking _Create launch template_.

3. Now go to _Auto Scaling Groups_ in the sidebar and click _Create Auto Scaling group_.
   * Use your unique name again.
   * Select the launch template you created.
   * Choose your VPC and **both** subnets.
   * To use your target group, check _Attach to an existing load balancer_ under _Load Balancing_ and select your target group.
   * Under _Health Checks_, check _Turn on Elastic Load Balancing health checks_.
   * Set the maximum capacity to `4`, the minimum to `0`, and the desired capacity to `2`.
   * Complete the process.

After creation, two instances should be generated according to your specifications after a short time.

#### Functional testing

> [!IMPORTANT]  
> If you are using macOS, this step does not work with Safari.  
> Please use a different browser for this step.

1. Go to your target group and click on the _Targets_ tab.  
   Your instances should appear here as _healthy_ after a short time.  
   If the instances do not appear or remain _unhealthy_ for more than 5 minutes, you likely made a mistake.

2. Go to the load balancer view, copy the _DNS name_, and open it in a new tab.  
   If you see a dragon, you are viewing the HTTP response from one of your instances.

3. Now connect to _one_ of your running instances via EC2 Instance Connect.
   * Go to _Instances_ and select (without clicking on the detail view) one of your instances.
   * Go to _Actions_ in the top right, select _Connect_, and then click _Connect_ again in the following view.
   * You are now connected to a shell session on the host.
   * A `httpd` service is running on the host, hosting the website.  
     Stop the process.
   * What do you observe with the target group, auto scaling group, and your instances? How does the website behave during this time?

Solution for stopping the process:

<details>
<p>

> Find the relevant process id, e.g. with `ps`:
>
> ``` shell
> ps aux | grep httpd
> ```
>
> Exemplary result:
>
> ``` text
> root        6634  0.0  0.1   2788  1660 ?        S    15:07   0:00 busybox httpd -f index.html -p 8080
> ubuntu      7287  0.0  0.0   7672   652 pts/0    S+   15:19   0:00 grep --color=auto httpd
> ```
>
> Kill the process `6634`, in this case requires root permissions:
>
> ``` shell
> sudo kill 6634
> ```
>
> close the window.

</p>
</details>

Solution of observation:

<details>
<p>

> First, your instance in the target group will become _Unhealthy_ because the application is no longer responding.  
> As a result, the load balancer will route all traffic to the remaining functioning instance.  
> The operation of the website remains uninterrupted; it continues to work.  
> After some time, the auto scaling group will respond to the status reported by the load balancer and replace the broken instance with a new one in the same zone.  
> The system heals itself.  
> For a short time, there will be three instances.  
> The broken instance will be seamlessly terminated.  
> The entire process usually takes a maximum of 10 minutes.
> 
</p>
</details>

### Working with the AWS CLI

#### Prerequisites

Build the container image `iaas-container` with:

``` shell
cd ../iaas-container
docker build \
  --build-arg IMAGE_CREATED="$(date -u +'%Y-%m-%dT%H:%M:%SZ')" \
  --build-arg IMAGE_REVISION="$(git rev-parse --abbrev-ref HEAD)" \
  --tag iaas-container \
  .
```

Start the container with:

``` shell
docker run -it --rm iaas-container
```

You should see an open bash in the container.
Enter `aws configure sso`.
Enter session name `my-sso`, sso start url `https://cc-th-rosenheim.awsapps.com/start`,
region `eu-central-1` and output format `json` and profile name `student`.
Leave other values untouched. 

Run: 
```shell
export AWS_DEFAULT_PROFILE=student
```

Run `aws sts get-caller-identity` for checking your configuration.
You should see an answer similar to the following:
``` json
{
  "UserId": "AIDAIVMF6UC5ZJA4LA2QU",
  "Account": "264524865537",
  "Arn": "arn:aws:iam::264524865537:user/akrause"
}
```

#### Working with the aws cli

In your Docker environment, the command-line program (`aws`) is installed, which allows you to connect to an EC2 instance via SSH.

Now, imagine you are responsible for the reliable operation of the _Dragon Service_ you created today on behalf of a sponsor.  
Your sponsor notices that the user numbers of your service are declining and therefore wants to save money—they are willing to accept lower availability.  
Reduce the number of instances in the auto scaling group to `1` instead of `2`.  
Use `aws autoscaling help` to put together a command.

Solution:

<details>
<p>

> ``` shell
> aws autoscaling set-desired-capacity \
>   --auto-scaling-group-name <YOUR UNIQUE NAME> \
>   --desired-capacity 1
> ```

</p>
</details>

To increase user numbers again, your sponsor wants to try a new format—penguins, he has heard, are very popular among computer scientists.  
Connect to the remaining instance.  
Make sure not to connect to the instance that may still be shutting down.

Insert your unique name into the following command and execute it to find your instance:
``` shell
aws ec2 describe-instances \
  --filters "Name=tag:aws:autoscaling:groupName,Values=<YOUR UNIQUE NAME>" "Name=instance-state-name,Values=running" \
  --query 'Reservations[].Instances[].InstanceId' \
  --output text
```

You may use the instance id to connect via `ec2-instance-connect`: 

``` shell
aws ec2-instance-connect ssh \
  --instance-id <INSTANZ ID> \
  --os-user ubuntu
```

Edit the `index.html` file under `/` so that a penguin appears—you will need root privileges for this.  
If you don’t know how to create a penguin, simply change the message from _Hello World_ to _I am a penguin_.

Solution:

<details>
<p>

> Edit the file using `nano`, for example:
> <pre>
> sudo nano /index.html
> </pre>
> Depending on your operating system, save your changes with CTRL+O (Windows) or Control+O (MacOS).

</p>
</details>

Go back to your load balancer’s address in the browser to check your change.

The penguin is well-received, and user numbers are increasing.  
Your sponsor wants to handle the load with an additional instance.  
Use the command line to scale your auto scaling group to `2`.

Solution:

<details>
<p>

> ``` shell
> aws autoscaling set-desired-capacity \
>   --auto-scaling-group-name <YOUR UNIQUE NAME> \
>   --desired-capacity 2
> ```

</p>
</details>

What will your users see once the additional instance has been successfully registered with the load balancer after a few minutes?

<details>
<p>

> The new instance was started with the unchanged launch template and is therefore configured with the dragon.
> The website alternates between the old and new versions of the service.
> The load balancer distributes the load alternately across the instances.
>
> If you want to change instances in an auto scaling group, you should first adjust the launch template and then replace the instances in a rolling manner.
>
> 1. First, add a new instance.
> 2. Wait until it has been tested by the load balancer and included in the rotation.
> 3. Take an old instance out of rotation (no new connections) and wait until all ongoing requests have been completed.
> 4. Shut down the old instance.
> 5. Repeat until there are no old instances left.
>
> Ideally, you have already configured an image created with Packer in the new launch template, so that the installation phase for dependencies is omitted and the same packages are always present.
> You can also start a rolling replacement without service interruption fully automatically:
> ``` shell
> aws autoscaling start-instance-refresh \
>   --auto-scaling-group-name <YOUR UNIQUE NAME>
> ```

</p>
</details>
