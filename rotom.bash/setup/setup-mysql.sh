#!/usr/bin/env bash

# TODO: remove database credentials
MYSQL_ROOT_USERNAME="root"
MYSQL_ROOT_PASSWORD="root"

export ROTOM_ACCOUNTS_USERNAME="rotom-accounts"
export ROTOM_ACCOUNTS_PASSWORD="password"

envsubst < setup-mysql.sql | mysql -u "$MYSQL_ROOT_USERNAME" "-p$MYSQL_ROOT_PASSWORD"