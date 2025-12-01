#!/usr/bin/env bash

sudo apt update
# MySQL
echo "BASH LOG: Downloading and installing MySQL"
wget https://dev.mysql.com/get/mysql-apt-config_0.8.36-1_all.deb
dpkg -i mysql-apt-config_0.8.36-1_all.deb
rm mysql-apt-config_0.8.36-1_all.deb
sudo apt update
dpkg-reconfigure mysql-apt-config
sudo apt update
sudo apt install mysql-server
systemctl status mysql