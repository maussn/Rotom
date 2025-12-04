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

echo "BASH LOG: Writing to database."

# TODO: remove database credentials
MYSQL_ROOT_USERNAME="root"
MYSQL_ROOT_PASSWORD="root"

export ROTOM_READER_ACCOUNTS_USERNAME="reader-accounts"
export ROTOM_READER_ACCOUNTS_PASSWORD="password"

export ROTOM_READER_ITEMS_USERNAME="reader-items"
export ROTOM_READER_ITEMS_PASSWORD="password"

export ROTOM_READER_LOANS_USERNAME="reader-loans"
export ROTOM_READER_LOANS_PASSWORD="password"

envsubst < rotom.bash/setup-mysql-databases.sql | mysql -u "$MYSQL_ROOT_USERNAME" "-p$MYSQL_ROOT_PASSWORD"

if $demo; then
  envsubst < rotom.bash/setup-mysql-demo.sql | mysql -u "$MYSQL_ROOT_USERNAME" "-p$MYSQL_ROOT_PASSWORD"
fi