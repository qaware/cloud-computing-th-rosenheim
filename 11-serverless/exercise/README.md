# Exercise: Serverless Computing with FaaS

## Goal

In this exercise you get familiar with **Function as a Service (FaaS)** on AWS, using AWS Lambda, the Amazon API Gateway and the serverless database Amazon DynamoDB.

You will build a small application step by step:

```
                        ┌──────────────────────┐
              GET /hello│ Lambda <NAME>-hello  │
           ┌───────────►│                      │
┌──────────┴──┐         └──────────────────────┘
│ API Gateway │
│ <NAME>-api  │         ┌──────────────────────┐     ┌──────────────────────┐
└──────────┬──┘         │ Lambda <NAME>-links  │     │ DynamoDB table       │
           └───────────►│                      ├────►│ <NAME>-links         │
     POST /links        └──────────────────────┘     └──────────────────────┘
     GET /{code}
```

1. A _Hello World_ function, reachable via an HTTP API.
2. An experiment that shows why functions should not keep state.
3. A tiny URL shortener (think bit.ly) that stores its links in DynamoDB.

For each task you can choose how to build the infrastructure:

- **Option A: AWS management console** - you click everything together yourself and see every piece.
- **Option B: Terraform** - you create exactly the same infrastructure as code, with the same names.

Both options lead to the same result. Pick the one you like, or do both: in that case, clean up the console resources (Task 5, option A) before you start with Terraform, since both use the same resource names.

## Prerequisites

- An AWS account. Either:
  - **Course account**: access via `https://cc-th-rosenheim.awsapps.com/start`, provided by your instructor, or
  - **Own account**: your personal [AWS account](https://aws.amazon.com/free/).
- `curl`, to call your API from the command line.
  On Windows PowerShell, use `curl.exe` instead of `curl`.
- **Option B only**:
  - [Terraform](https://developer.hashicorp.com/terraform/install) `1.8.0` or newer. Check with `terraform version`.
  - The [AWS CLI v2](https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html). Check with `aws --version`.
    If you use your own AWS account, you need version `2.32.0` or newer.

> [!NOTE]
> **Own account only:** Lambda, API Gateway and DynamoDB (on-demand) are billed per request.
> For this exercise the costs are a fraction of a cent, and mostly covered by the AWS Free Tier.
> Still, clean up when you are done (Task 5), and consider setting up a [budget alert](https://docs.aws.amazon.com/cost-management/latest/userguide/budgets-create.html).

**Notes**:

- Make sure the AWS Region is set to **eu-central-1 (Frankfurt)** (top right of the console).
- Choose a **unique name**, e.g. `lbuchner` for `Lukas Buchner`, and use it for every resource you create.
  In the course account all participants share the same account, so without unique names you and your classmates will not find your resources, or even overwrite each other's.
  Use lowercase letters, digits and `-` only.
- In the following, `<NAME>` refers to your unique name.
- Log in to the management console:
  - **Course account**: open `https://cc-th-rosenheim.awsapps.com/start` and click on `student-permissions-set`.
  - **Own account**: sign in at `https://console.aws.amazon.com`.

## Task 1: Hello World

In this task you create a lambda function and make it accessible from the internet via an HTTP API in the API Gateway.

You will create:

| Resource | Name |
|---|---|
| CloudWatch log group for the function logs, retention 1 week | `/aws/lambda/<NAME>-hello` |
| CloudWatch log group for the API access logs, retention 1 week | `/aws/api_gw/<NAME>-api` |
| Lambda function, runtime Node.js 24.x | `<NAME>-hello` |
| HTTP API with the route `GET /hello` and the stage `$default` | `<NAME>-api` |

### Option A: Management console

**Log groups**

Lambda functions and the API Gateway write their logs to CloudWatch Logs. By default, these logs are kept forever.
We create the log groups upfront, so we can limit how long logs are kept.

1. Open the _CloudWatch_ service and choose _Logs_ > _Log groups_ > _Create log group_.
2. Create a log group `/aws/lambda/<NAME>-hello` with _Retention setting_ `1 week`.
3. Create a second log group `/aws/api_gw/<NAME>-api` with _Retention setting_ `1 week`.

**Lambda function**

1. Open the _Lambda_ service and choose _Create function_ > _Author from scratch_.
2. Configure:
   - _Function name_: `<NAME>-hello`
   - _Runtime_: `Node.js 24.x`
   - Expand _Change default execution role_. The execution role is used by your function to authenticate against other AWS services. Here it is needed to ship the logs to CloudWatch.
     - **Course account**: choose _Use an existing role_ and select `BasicLambdaExecutionRole`.
     - **Own account**: choose _Create a new role with basic Lambda permissions_.

   > [!NOTE]
   > In the course account you don't have permissions to create new roles. Do **not** choose _Create a new role_ there.

3. Choose _Create function_.
4. Have a look at the code in the editor. It is the same as in [`src/hello.mjs`](./src/hello.mjs).
5. Use the _Test_ tab to invoke your function once. Then open the log group `/aws/lambda/<NAME>-hello` in CloudWatch and find the log of your invocation.

**HTTP API**

AWS offers a few ways to make a lambda function accessible from the internet. One way is the serverless API Gateway.

1. Open the _API Gateway_ service, choose _Create API_ and _Build_ on **HTTP API**.
2. _Integrations_: choose _Add integration_ > _Lambda_ and select your function `<NAME>-hello`.
3. _API name_: `<NAME>-api`. Choose _Next_.
4. _Configure routes_: method `GET`, resource path `/hello`, integration target `<NAME>-hello`. Choose _Next_.
5. _Configure stages_: keep the stage `$default` with _Auto-deploy_ enabled. Choose _Next_ and _Create_.

   The console automatically grants the API Gateway the permission to invoke your function.

6. Enable access logs: in the navigation pane of your API choose _Monitor_ > _Logging_, select the stage `$default` and choose _Edit_.
   - Enable _Access logging_.
   - _Log destination_: the ARN of your log group `/aws/api_gw/<NAME>-api`. You find it in the details of the log group in CloudWatch. Remove the trailing `:*`.
   - _Log format_:

     ```json
     {"requestId":"$context.requestId","sourceIp":"$context.identity.sourceIp","requestTime":"$context.requestTime","protocol":"$context.protocol","httpMethod":"$context.httpMethod","resourcePath":"$context.resourcePath","routeKey":"$context.routeKey","status":"$context.status","responseLength":"$context.responseLength","integrationErrorMessage":"$context.integrationErrorMessage"}
     ```

   - Choose _Save_.
7. In the navigation pane choose _Stages_ and click on `$default`. Note down the _Invoke URL_.

Continue with [Try it](#try-it).

### Option B: Terraform

**Set up the AWS CLI**

Terraform uses the same credentials as the AWS CLI.
Configure an AWS CLI profile called `student`, as described in detail in [Task 1 of the PaaS exercise](../../06-paas/exercise/01-aws/README.md#task-1-set-up-the-aws-cli):

- **Course account**: `aws configure sso` with session name `my-sso`, SSO start URL `https://cc-th-rosenheim.awsapps.com/start`, SSO region `eu-central-1`, default client region `eu-central-1`, output format `json` and profile name `student`.
- **Own account**: `aws login --profile student`, region `eu-central-1`.

Make the profile the default for the current shell and check it:

``` shell
# macOS / Linux
export AWS_PROFILE=student
```

``` powershell
# Windows PowerShell
$env:AWS_PROFILE = "student"
```

``` shell
aws sts get-caller-identity
```

> [!TIP]
> If Terraform later complains about missing or expired credentials, log in again (`aws sso login --profile student` or `aws login --profile student`).
> If it still can't find any credentials, export them as environment variables, which every tool understands:
> `eval "$(aws configure export-credentials --profile student --format env)"` (macOS / Linux) or
> `aws configure export-credentials --profile student --format powershell | Invoke-Expression` (Windows PowerShell).

**Configure the variables**

Create a file `terraform.tfvars` in the [`src`](./src) directory with your unique name and the account type:

``` hcl
name           = "<NAME>"
course_account = true   # false if you use your own AWS account
```

The file is ignored by Git, see [`src/.gitignore`](./src/.gitignore).

**Read the code**

Get familiar with the Terraform code in the [`src`](./src) directory. Find the resource for each of the console steps of option A:

- [`main.tf`](./src/main.tf): the Terraform and AWS provider versions, and the region.
- [`variables.tf`](./src/variables.tf): the two variables you just set.
- [`iam.tf`](./src/iam.tf): the execution roles of the functions.
  In the course account, the existing roles are looked up, because you can't create roles there.
  In your own account, Terraform creates them - that's what _Create a new role with basic Lambda permissions_ does in the console.
- [`lambda.tf`](./src/lambda.tf): the function `<NAME>-hello` and its log group.
- [`api_gw.tf`](./src/api_gw.tf): the API, its stage with access logging, the route, the integration, the log group and the permission for the API Gateway to invoke the function.
- [`outputs.tf`](./src/outputs.tf): values printed after `terraform apply`.

For now, ignore `dynamodb.tf` and `links.tf`, they are part of Task 3. Terraform will create them anyway, which is fine.

If you need further guidance, have a look at this [tutorial](https://developer.hashicorp.com/terraform/tutorials/aws/lambda-api-gateway) from HashiCorp. It creates a similar setup and has extensive explanations.

**Create the infrastructure**

Navigate into the `src` directory in your terminal and run `terraform init`.
This downloads the providers required for interacting with AWS.

Once it successfully initialized, run `terraform plan`.
This shows you what Terraform intends to do.
Read the output carefully and decide whether everything looks the way it is supposed to.
Once you're happy with the plan, run `terraform apply`.
This prints the plan again and asks for your permission to proceed.
This step actually creates/updates/deletes infrastructure.

Enter `yes` and wait until the infrastructure is created.
Note down the output `gateway_domain`, it is the _Invoke URL_ of your API.

### Try it

Open `<Invoke URL>/hello` in your browser, or call it with curl:

``` shell
curl https://<your-api-domain>/hello
```

You should see `"Hello from Lambda!"`.

Have a look at the log groups `/aws/lambda/<NAME>-hello` and `/aws/api_gw/<NAME>-api` in CloudWatch. What is logged where?

## Task 2: Where does state live?

So far, our function doesn't remember anything between two calls.
Real applications need state - so where does it go in a serverless world?

Let's try the naive approach first.
Change the function so it counts how often it was called, using a variable _outside_ of the handler:

```js
let counter = 0;

export const handler = async (event) => {
    counter++;
    return { statusCode: 200, body: JSON.stringify(`Hello from Lambda! Call #${counter}`) };
};
```

Deploy the change:

- **Option A**: edit the code in the editor of the Lambda console and choose _Deploy_.
- **Option B**: edit [`src/hello.mjs`](./src/hello.mjs) and run `terraform plan` and `terraform apply`.
  Take a close look at the plan. What does Terraform change, and how does it know that the code changed?

Call `GET /hello` a few times in your browser. Looks like it works?
Now fire 50 requests in parallel:

``` shell
# macOS / Linux
seq 50 | xargs -P 25 -I{} curl -s https://<your-api-domain>/hello; echo
```

``` powershell
# Windows PowerShell 7
1..50 | ForEach-Object -Parallel { curl.exe -s https://<your-api-domain>/hello } -ThrottleLimit 25
```

Look at the numbers.
Then wait ~10 minutes without calling the function (or simply deploy it again) and try again.

Questions:

- Why do some numbers appear several times?
- Why does the counter start from 1 again after a while?
- What does this tell you about where state must live in a FaaS application?

<details>
<summary>Solution</summary>

> Lambda runs your function in _execution environments_. One environment handles one request at a time.
> When several requests arrive at the same time, Lambda starts additional environments (it scales out). Each of them has its own copy of `counter`, so the numbers overlap.
>
> Environments that are idle for a while are shut down, and every deployment replaces them.
> The next request then hits a fresh environment (a _cold start_), in which `counter` starts at 0 again.
>
> Functions must therefore be **stateless**: anything that must survive a single request belongs into an external service, e.g. a database, a cache or an object storage.

</details>

## Task 3: A URL shortener with DynamoDB

We will build a tiny URL shortener that stores its links in [DynamoDB](https://aws.amazon.com/dynamodb/), AWS's serverless key-value and document database.
Like Lambda, DynamoDB has no servers for you to manage, scales automatically and - in on-demand mode - you only pay per request.

| Route | What it does | DynamoDB operation |
|---|---|---|
| `POST /links` with body `{"url": "https://..."}` | generates a short code and stores the link | `PutItem` |
| `GET /{code}` | redirects (HTTP 301) to the stored URL and counts the click | `UpdateItem` |

Each link is stored as an item like `{ code: "aB3x-9", url: "https://...", clicks: 4, createdAt: "..." }`.

**Read the code**

Read the function's code in [`src/links.mjs`](./src/links.mjs) and answer these questions:

- The table name is passed to the function as the environment variable `TABLE_NAME` instead of being written into the code. Why?
- Why is the DynamoDB client created _outside_ of the handler, even though we just learned not to keep state there?
- `followLink` reads the URL and increments `clicks` in a single `UpdateCommand`. What could go wrong if it first read the item, added 1 in JavaScript and then wrote it back?

<details>
<summary>Solution</summary>

> Configuration that differs between deployments (other students, test and production stages, ...) belongs into the environment, not into the code.
> The same code can then be deployed anywhere ([twelve-factor app](https://12factor.net/config)).
>
> The client is not application state, but a reusable connection setup. Creating it is relatively expensive, so reusing it in a warm execution environment makes subsequent requests faster.
> If it is lost in a cold start, nothing breaks - a new one is simply created.
>
> Two execution environments could read the same value at the same time, e.g. `4`, both add 1 and both write `5`. One click is lost (_lost update_).
> With `ADD clicks :one`, DynamoDB performs the increment atomically on the server side.

</details>

You will create:

| Resource | Name |
|---|---|
| DynamoDB table, partition key `code` (String), capacity mode on-demand | `<NAME>-links` |
| CloudWatch log group, retention 1 week | `/aws/lambda/<NAME>-links` |
| Lambda function, runtime Node.js 24.x, environment variable `TABLE_NAME` = `<NAME>-links`, with permission to read and write items in the table | `<NAME>-links` |
| Routes `POST /links` and `GET /{code}` in your existing API, integrated with the new function | - |

### Option A: Management console

**DynamoDB table**

1. Open the _DynamoDB_ service and choose _Tables_ > _Create table_.
2. Configure:
   - _Table name_: `<NAME>-links`
   - _Partition key_: `code`, type `String`. No sort key.
   - _Table settings_: choose _Customize settings_ and make sure the _Capacity mode_ is **On-demand**. Leave all other settings unchanged.

   Note that you only define the key here. All other attributes (`url`, `clicks`, ...) are schemaless and simply added with each item.
3. Choose _Create table_ and wait until its status is _Active_.

**Log group**

Create a log group `/aws/lambda/<NAME>-links` with retention `1 week` in CloudWatch, as in Task 1.

**Lambda function**

1. Create a new function from scratch:
   - _Function name_: `<NAME>-links`
   - _Runtime_: `Node.js 24.x`
   - _Execution role_:
     - **Course account**: _Use an existing role_ `DynamoLambdaExecutionRole`. In addition to writing logs, this role allows reading and writing items in DynamoDB tables.
     - **Own account**: _Create a new role with basic Lambda permissions_. You will add the DynamoDB permissions in step 4.
2. Replace the code in the editor with the content of [`src/links.mjs`](./src/links.mjs) and choose _Deploy_.
3. Choose the _Configuration_ tab > _Environment variables_ > _Edit_ and add the variable `TABLE_NAME` with the value `<NAME>-links`. Save.
4. **Own account only**: allow the function to access your table.
   1. Choose the _Configuration_ tab > _Permissions_ and click on the role name. This opens the role in the IAM console.
   2. Choose _Add permissions_ > _Create inline policy_ and switch to the _JSON_ editor.
   3. Paste the following policy. Replace `<TABLE_ARN>` with the ARN of your table, which you find in the DynamoDB console under _Tables_ > `<NAME>-links` > _General information_.

      ```json
      {
        "Version": "2012-10-17",
        "Statement": [{
          "Effect": "Allow",
          "Action": ["dynamodb:GetItem", "dynamodb:PutItem", "dynamodb:UpdateItem"],
          "Resource": "<TABLE_ARN>"
        }]
      }
      ```

      This follows the _principle of least privilege_: the function may only perform the item operations it needs, and only on this table.
   4. Name the policy `<NAME>-links-dynamodb` and create it.

**Routes**

1. Open your API `<NAME>-api` in the API Gateway console and choose _Develop_ > _Routes_ > _Create_.
2. Create the route `POST` `/links`.
3. Create the route `GET` `/{code}`. `{code}` is a _path parameter_: it matches any path like `/aB3x-9`. More specific routes like `GET /hello` still take precedence.
4. Select the route `POST /links` and choose _Attach integration_ > _Create and attach an integration_:
   - _Integration type_: `Lambda function`
   - _Lambda function_: `<NAME>-links`
   - Keep _Grant API Gateway permission to invoke your Lambda function_ enabled.
5. Select the route `GET /{code}`, choose _Attach integration_ and select the integration you just created.

As _Auto-deploy_ is enabled on the `$default` stage, the routes are live right away.

### Option B: Terraform

The new parts of the infrastructure are:

- [`dynamodb.tf`](./src/dynamodb.tf): the DynamoDB table.
- [`links.tf`](./src/links.tf): the function `<NAME>-links`, its log group, the integration, the routes and the permission for the API Gateway.
- [`iam.tf`](./src/iam.tf): the execution role of the function.
  In the course account the existing role `DynamoLambdaExecutionRole` is looked up.
  In your own account Terraform creates a role with an inline policy that grants access to exactly this table - just like option A.

Read through the files and find the resource for each of the console steps of option A.

If you followed Task 1 with Terraform, these resources already exist. Otherwise, run `terraform init`, `terraform plan` and `terraform apply` as described in [Task 1](#option-b-terraform).

### Try it

Create a short link:

``` shell
curl -X POST https://<your-api-domain>/links \
  -H 'content-type: application/json' \
  -d '{"url": "https://www.th-rosenheim.de"}'
```

``` powershell
# Windows PowerShell
Invoke-RestMethod -Method Post -Uri https://<your-api-domain>/links -ContentType application/json -Body '{"url": "https://www.th-rosenheim.de"}'
```

Open the returned `shortUrl` in your browser - you should be redirected.

Now open the DynamoDB console, select your table and choose _Explore table items_.
You should see your link and its click counter. Click your short link a few more times and refresh the view.

Repeat the parallel experiment from Task 2 against your short link and check that no click got lost:

``` shell
seq 50 | xargs -P 25 -I{} curl -s -o /dev/null https://<your-api-domain>/<code>
```

## Task 4: Get creative

Pick one or more of the following extensions. Implement them with the option of your choice.

- Add a route `GET /links/{code}/stats` that returns the URL and its number of clicks (there is a `TODO` in `links.mjs`). Use a `GetCommand`.
- Let links expire: store an `expiresAt` attribute (unix timestamp in seconds) and enable DynamoDB's [time to live](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/TTL.html) on the table: in the console under _Additional settings_ of your table, or via the `ttl` block of the `aws_dynamodb_table` resource.
  Hint: DynamoDB deletes expired items only eventually, so `followLink` should also check the expiry itself.
- Allow custom codes, e.g. `{"url": "...", "code": "cc-lecture"}`. What happens if the code is already taken?
- Create a further lambda function that does whatever you'd like it to do, reachable under `POST /awesome`.
  How about getting polyglot and writing it in Python?

## Task 5: Clean up

Delete everything you created, using the same option you created it with.

### Option A: Management console

1. **API Gateway**: delete your API `<NAME>-api`. This also deletes its routes, integrations and stage.
2. **Lambda**: delete the functions `<NAME>-hello` and `<NAME>-links`.
3. **DynamoDB**: delete the table `<NAME>-links`. Confirm that you don't want to create a backup.
4. **CloudWatch**: delete the log groups `/aws/lambda/<NAME>-hello`, `/aws/lambda/<NAME>-links` and `/aws/api_gw/<NAME>-api`.
5. **Own account only**: in the IAM console, delete the roles that the Lambda console created for you (`<NAME>-hello-role-...` and `<NAME>-links-role-...`).

### Option B: Terraform

Run `terraform destroy` in the `src` directory, check the plan and confirm with `yes`.
In your own account this also deletes the IAM roles created by Terraform.

---

**Course account**: do **not** delete the roles `BasicLambdaExecutionRole` and `DynamoLambdaExecutionRole`, other students use them as well.

Finally, if you used option B, run `aws logout --profile student` (own account) or `aws sso logout --profile student` (course account) to clear the CLI login.
