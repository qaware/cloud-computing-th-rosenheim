#!/bin/bash
set -euxo pipefail

apt update
DEBIAN_FRONTEND=noninteractive apt install -y -q \
    python3 \
    python3-pip \
    python-is-python3 \
    git \
    unzip \
    curl \
    groff \
    wget \
    lsb-release

wget -O- https://apt.releases.hashicorp.com/gpg | gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | tee /etc/apt/sources.list.d/hashicorp.list

apt update
apt install -y -q terraform


mkdir bin 
cd bin

git clone https://github.com/aws/aws-ec2-instance-connect-cli.git 
cd aws-ec2-instance-connect-cli 
pip3 install -r requirements.txt 
pip3 install -e . 
cd - 

curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" 
unzip awscliv2.zip 
rm awscliv2.zip 
./aws/install

cd ~

cat >> /root/.bashrc <<-EOF
    alias mssh=/root/bin/aws-ec2-instance-connect-cli/bin/mssh
    export AWS_DEFAULT_REGION=us-east-1
    complete -C /usr/local/bin/aws_completer aws
    alias configure=/root/bin/configure.sh
EOF

pip install terraform-local
pip install awscli-local[ver1]