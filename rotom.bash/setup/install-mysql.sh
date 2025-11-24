#!/usr/bin/env bash

apt update
# MySQL
echo "Downloading and installing MySQL"
wget https://dev.mysql.com/get/mysql-apt-config_0.8.36-1_all.deb
dpkg -i mysql-apt-config_0.8.36-1_all.deb
rm mysql-apt-config_0.8.36-1_all.deb
apt update
dpkg-reconfigure mysql-apt-config
apt update
apt install mysql-server
systemctl status mysql
./setup-mysql.sh