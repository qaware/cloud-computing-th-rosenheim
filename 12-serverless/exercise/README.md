# Exercise: Serverless Computing mit FaaS

The goal of this lecture is to get you familiar with `Function as a Service (FaaS)` on AWS with AWS Lambda and the API gateway.

## FaaS on AWS - manual setup

Login with your student account at https://cc-th-rosenheim.awsapps.com/start/#. 
Click on the `student-permissions-set` link. 
You should now see the AWS management console. 

### Creating the lambda function
Navigate to the lambda console, by searching for lambda in the search bar.
Make sure you are in the region `eu-central-1`.
Create a new function without template, using the following settings: 
- Use a unique name based on your credentials, e.g. lubuHelloWorld
- Use the existing role `BasicLambdaExecutionRole`. This role is used by your lambda function to authenticate against other AWS services. Here it is used, to ship the execution logs to cloudwatch.

Click `Create Function`. 

The function is now deployed. Feel free to test the function by using the `test` functionality.

### Creating the API Gateway
AWS offers few ways to make a lambda function accessible from the internet. 
One way is to use the serverless API gateways. 

In this part of the exercise you will set up an API gateway that is exposed to the internet, including a route that will trigger the lambda function you created above. 

Navigate to the API gateway console by searching for API gateway in the search bar. 

Create a new API with the following settings: 
1. Http API
2. Use a unique API name based on your credentials, e.g. lubusAPI. Add your lambda function as integration. 
3. Configure a route of your choice - ideally you use `GET` to make it easily callable from your browser.
4. Leave the default settings for stages

Once the API is created, it will automatically be provisioned. 
Before you can call the deployed API, we need to find the domain its hosted on. 
Therefore, navigate to `stages` in the left sidebar. 
You should see a single entry called `$default`. Click on it. 
On the right side you should now see the domain. 

Enter the domain into a new browser tab and append the route you created. 
You should see `Hello world from Lambda`. 

You've reached the end of this part, great! Now delete your API Gateway and lambda function.

## FaaS on AWS - setup with terraform

In this part of the exercise you will recreate exactly the same setup as above using terraform and infrastructure as code. 

[Install](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli) terraform, if it isn't on your system. 

Get yourself familiar with the terraform code in the `/src` directory. 
Fill in all marked `TODO` fields. 
In case you need further guidance, ask your instructor and look at this [tutorial](https://learn.hashicorp.com/tutorials/terraform/lambda-api-gateway
) from hashicorp. It creates a similar infrastructure set up and has extensive explanations.

### Working with Terraform

Now let's try to create the described infrastructure. 

#### Authentication
Before you can use terraform, you have to set up credentials that can be used to authenticate against the AWS APIs.
Open https://cc-th-rosenheim.awsapps.com/start/# in your browser. 
This time click on `access-keys` and follow option 1, using AWS environment variables. 
Terraform supports these variables out of the box. 
Export the env variables in the terminal session you want to use terraform in. 

#### Creating infrastructure
Navigate into the `/src` directory in your terminal and run `terraform init`. 
This will download some packages required for interacting with aws. 

Once it successfully initialized, run `terraform plan`. 
This shows you, what terraform intends to do. 
Read the output carefully and decide, on whether everything looks the way it is supposed to be. 
Once you're happy with the plan, run `terraform apply`. 
This will print the plan again and asks for your permissions to proceed. 
This step actually creates/updates/deletes some infrastructure. 

Enter `yes` and wait until the infrastructure is created. 
Try to call the new TF API in your browser. 
You should see `Hello from terraform Lambda!`. 

#### Make changes to the infrastructure
Adapt the code of the lambda function and deploy the changes with terraform using `plan` and `apply`. 
Take a close look at the plan again and try to make sense of it. 

#### Get creative
Extend the infrastructure by creating a second lambda function that does whatever you'd like it to do. 
It should be reachable under POST /awesome.

How about getting polyglot and writing the second function in python?

#### Cleanup

Once you are done, destroy all of your infrastructure by running `terraform destroy`.