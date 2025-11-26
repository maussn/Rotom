#!/usr/bin/env bash

show_help() {
  printf "Usage: %s [-d] [-h]\n\nOptions\n\t-d\t\tSetup in demo mode.\n\t-h\t\tShow this help and exit.\n" "$0"
}

demo=false

while getopts ":dh" opt; do
  case $opt in
    d) demo=true ;;
    h) show_help; exit 0 ;;
    \?) echo "Unknown option: -$OPTARG" >&2; exit 1 ;;
  esac
done

shift $((OPTIND - 1))

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

if $demo; then
  ./setup-mysql.sh -d
else
  ./setup-mysql.sh
fi