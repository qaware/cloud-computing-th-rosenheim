# **Exercise: Virtualization with Vagrant**

In this exercise, you’ll use **Vagrant** to set up a simple development stack consisting of **NGINX**, **PHP**, and **MySQL** — all running inside a virtual machine.

---

## **Preparation**

Before starting, make sure that:

1. **Vagrant** and a compatible virtualization provider (such as **VirtualBox**, **Hyper-V**, **KVM**, **xhyve**, or **Parallels**) are installed and working properly on your system.
2. You can verify this by running:

   ```bash
   vagrant --version
   ```

   and checking that your virtualization software starts correctly.

---

## **Tasks**

### **Task 1: Setting up Vagrant and a Virtual Machine**

In this part, you’ll create and configure an Ubuntu 22.04 virtual machine using Vagrant.

#### **Step 1: Initialize a Vagrant Box**

Run the following command to initialize a base Ubuntu 24.04 box: 

```bash
vagrant init bento/ubuntu-24.04
```

This will create a `Vagrantfile` in your current directory.

Next, open the `Vagrantfile` and configure the VM to use **1024 MB of RAM**:

```ruby
config.vm.box_check_update = false
config.vm.provider "virtualbox" do |vb|
  vb.memory = "1024"
end
```

Then start your VM and connect to it via SSH:

```bash
vagrant up --provider virtualbox
vagrant ssh
```

---

#### **Step 2: Installing NGINX with a Shell Provisioner**

We’ll now install the **NGINX web server** using Vagrant’s **Shell Provisioner**.

Update your `Vagrantfile` to:

* Forward port **80** from the guest VM to port **8080** on your host.
* Install and start NGINX automatically during provisioning.

```ruby
config.vm.network "forwarded_port", guest: 80, host: 8080, host_ip: "127.0.0.1"

config.vm.provision "shell", inline: <<-SHELL
  sudo apt-get update
  sudo apt-get install -y nginx
  sudo systemctl start nginx
SHELL
```

Run the provisioning process:

```bash
vagrant provision
```

or

```bash
vagrant reload --provision
```

To verify the installation, open a browser on your host machine and visit:

```
http://localhost:8080/
```

You should see the default NGINX welcome page.

---

#### **Step 3: Installing MySQL**

Next, we’ll install the **MySQL server** and **client** packages. Add the following commands to your provisioner:

```bash
# MySQL
debconf-set-selections <<< 'mysql-server mysql-server/root_password password secret'
debconf-set-selections <<< 'mysql-server mysql-server/root_password_again password secret'
sudo apt-get install -y mysql-server mysql-client
sudo systemctl start mysql
```

Then re-run the provisioning and verify that MySQL is running:

```bash
vagrant provision
vagrant ssh
mysql -u root -p
# Password: secret
```

---

#### **Step 4: Installing PHP and Configuring NGINX**

Finally, install **PHP** and configure NGINX to serve PHP files.

Extend your provisioner with:

```bash
# PHP
sudo apt-get install -y php php-fpm php-mysql
sudo systemctl start php8.3-fpm
```

Next, copy the configuration and example files from your host (in the same directory as your `Vagrantfile`) into the VM:

```bash
sudo mkdir -p /usr/share/nginx/www
sudo cp /vagrant/vagrant/default /etc/nginx/sites-available/default
sudo cp /vagrant/vagrant/info.php /usr/share/nginx/www/info.php
sudo cp /vagrant/vagrant/index.html /usr/share/nginx/www/index.html

sudo systemctl reload nginx
```

Then run:

```bash
vagrant provision
vagrant ssh
wget localhost:80/info.php
```

If everything worked, you should see PHP information output when visiting:

```
http://localhost:8080/info.php
```

---

## **Resources**

You are encouraged to explore and troubleshoot independently using online resources. Here are some useful links:

* [Vagrant Documentation](https://docs.vagrantup.com)
* [Vagrantfile Reference](https://www.vagrantup.com/docs/vagrantfile)
* [Vagrant CLI Commands](https://www.vagrantup.com/docs/cli)
