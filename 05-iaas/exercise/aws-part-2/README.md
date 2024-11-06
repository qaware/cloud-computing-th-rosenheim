# Exercise: Infrastructure as Code with Terraform on AWS

The goal of this exercise is to learn fundamental Infrastructure as Code skills with Terraform on the AWS Cloud.  
You will recreate the architecture from the previous exercise using Terraform.  
Basic steps have already been prepared for this.

1. Start the `iaas-container` and mount the `exercise/aws-part-2` directory in the container.  
   Example with Bash from the directory:

   ``` shell
   docker run -it --rm -w /code --mount type=bind,source="$(pwd)",target=/code iaas-container
   ```

2. Configure your AWS access with `aws configure sso` as in part-1.  
   Enter session name `my-sso`, SSO start URL `https://cc-th-rosenheim.awsapps.com/start`, region `eu-central-1`, output format `json`, and profile name `student`. Leave other values unchanged.

3. Initialize the working directory with `terraform init`.

4. Review the existing Terraform files and familiarize yourself with the basic structure.

5. Implement all sections marked with `#ToDo`.  
   Define a sensible order for yourself.  
   You should regularly apply your implementations to the AWS Cloud with `terraform apply`.

6. At the end, the Terraform configuration should contain an output parameter with a valid and functional URL.

7. Create a new workspace with `terraform workspace new dev` and switch to it with `terraform workspace select dev`.  
   Check whether you can create a second environment with `terraform apply`.  
   If not, adjust your configurations to make this possible.  
   Make resource names dependent on the workspace used.

8. Destroy all created resources with `terraform destroy` in both workspaces.