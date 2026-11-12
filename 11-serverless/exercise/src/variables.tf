### Input variables. Set them in a file `terraform.tfvars` (see README), or terraform will ask for them.

variable "name" {
  description = "Your unique name, e.g. lbuchner. Used as prefix for all resources."
  type        = string

  validation {
    condition     = can(regex("^[a-z0-9-]+$", var.name))
    error_message = "Use lowercase letters, digits and '-' only."
  }
}

variable "course_account" {
  description = "true: you use the course account (shared IAM roles already exist). false: you use your own AWS account (IAM roles are created)."
  type        = bool
}
