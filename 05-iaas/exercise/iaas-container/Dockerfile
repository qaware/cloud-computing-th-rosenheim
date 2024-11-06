# syntax=docker/dockerfile:1

# https://github.com/bridgecrewio/checkov/blob/master/docs/5.Policy%20Index/all.md
#checkov:skip=CKV_DOCKER_2: Ensure that HEALTHCHECK instructions have been added to container images
#checkov:skip=CKV_DOCKER_3: Ensure that a user for the container has been created

# https://hub.docker.com/_/ubuntu/tags
FROM ubuntu:24.04@sha256:3f85b7caad41a95462cf5b787d8a04604c8262cdcdf9a472b8c52ef83375fe15

ARG IMAGE_CREATED="1970-01-01T00:00:00Z"
ARG IMAGE_REVISION="master"

LABEL org.opencontainers.image.title="cloud-computing-th-rosenheim/iaas-container" \
      org.opencontainers.image.description="Cloud Computing - IaaS Container" \
      org.opencontainers.image.authors="Lukas Buchner" \
      org.opencontainers.image.created="${IMAGE_CREATED}" \
      org.opencontainers.image.url="https://github.com/qaware/cloud-computing-th-rosenheim/tree/master/05-iaas" \
      org.opencontainers.image.source="https://github.com/qaware/cloud-computing-th-rosenheim/tree/master/05-iaas/exercise/iaas-container" \
      org.opencontainers.image.revision="${IMAGE_REVISION}" \
      org.opencontainers.image.vendor="University of Applied Sciences Rosenheim" \
      org.opencontainers.image.documentation="https://github.com/qaware/cloud-computing-th-rosenheim/tree/master/05-iaas/exercise/iaas-container"

WORKDIR /root

# DL4006: Set the SHELL option -o pipefail before RUN with a pipe in
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

################################################################################

# Install required packages

# https://packages.ubuntu.com/noble/gnupg
# renovate: datasource=repology depName=ubuntu_24_04/gnupg versioning=loose
ENV GNUPG_VERSION="2.4.4"

# https://packages.ubuntu.com/noble/ca-certificates
# renovate: datasource=repology depName=ubuntu_24_04/ca-certificates versioning=loose
ENV CA_VERSION="20240203"

# https://packages.ubuntu.com/noble/curl
# renovate: datasource=repology depName=ubuntu_24_04/curl versioning=loose
ENV CURL_VERSION="8.5.0"

# https://packages.ubuntu.com/noble/wget
# renovate: datasource=repology depName=ubuntu_24_04/wget versioning=loose
ENV WGET_VERSION="1.21.4"

# https://packages.ubuntu.com/noble/git
# renovate: datasource=repology depName=ubuntu_24_04/git versioning=loose
ENV GIT_VERSION="2.43.0"

# https://packages.ubuntu.com/noble/unzip
# renovate: datasource=repology depName=ubuntu_24_04/unzip versioning=loose
ENV UNZIP_VERSION="6.0"

# https://packages.ubuntu.com/noble/groff
# renovate: datasource=repology depName=ubuntu_24_04/groff versioning=loose
ENV GROFF_VERSION="1.23.0"

# https://packages.ubuntu.com/noble/less
# renovate: datasource=repology depName=ubuntu_24_04/less versioning=loose
ENV LESS_VERSION="590"

# https://packages.ubuntu.com/noble/lsb-release-minimal
# renovate: datasource=repology depName=ubuntu_24_04/lsb-release versioning=loose
ENV LSB_VERSION="12.0"

RUN apt-get update \
 && apt-get install --yes --quiet --no-install-recommends \
      gnupg="${GNUPG_VERSION}-*" \
      ca-certificates="${CA_VERSION}" \
      curl="${CURL_VERSION}-*" \
      wget="${WGET_VERSION}-*" \
      git="1:${GIT_VERSION}-*" \
      unzip="${UNZIP_VERSION}-*" \
      groff="${GROFF_VERSION}-*" \
      less="${LESS_VERSION}-*" \
      lsb-release="${LSB_VERSION}-*" \
      openssh-client \
 && rm -rf /var/lib/apt/lists/*

################################################################################

# Install Terraform CLI

# https://github.com/hashicorp/terraform/tags
# renovate: datasource=github-tags depName=hashicorp/terraform
ENV TF_VERSION="1.8.4"

# Source: https://apt.releases.hashicorp.com/gpg
COPY terraform-cli-pub.key .

RUN gpg \
      --output /usr/share/keyrings/hashicorp-archive-keyring.gpg \
      --dearmor terraform-cli-pub.key \
 && echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" \
      > /etc/apt/sources.list.d/hashicorp.list \
 && apt-get update \
 && apt-get install --yes --quiet --no-install-recommends \
      terraform="${TF_VERSION}-*" \
 && rm -rf /var/lib/apt/lists/*

################################################################################

# Install AWS CLI

# https://github.com/aws/aws-cli/tags
# renovate: datasource=github-tags depName=aws/aws-cli
ENV AWS_VERSION="2.15.56"

# Source: https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html
COPY aws-cli-pub.key .

RUN gpg --import aws-cli-pub.key \
 && curl "https://awscli.amazonaws.com/awscli-exe-linux-$(uname -m)-${AWS_VERSION}.zip" -o "awscliv2.zip" \
 && curl "https://awscli.amazonaws.com/awscli-exe-linux-$(uname -m)-${AWS_VERSION}.zip.sig" -o "awscliv2.sig" \
 && gpg --verify awscliv2.sig awscliv2.zip \
 && unzip awscliv2.zip \
 && rm awscliv2.zip \
 && ./aws/install \
 && echo "complete -C /usr/local/bin/aws_completer aws" >> .bashrc
