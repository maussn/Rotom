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

if ! command -v mysql >/dev/null 2>&1; then
  echo "BASH LOG: No MySQL detected"
  ./rotom.bash/install-mysql.sh
fi

if ! command -v npm >/dev/null 2>&1; then
  echo "BASH LOG: No NPM detected"
  ./rotom.bash/install-nodejs.sh
fi

# Install project dependencies
(cd rotom.client || exit
npm install)

if ! command -v scala >/dev/null 2>&1; then
  echo "BASH LOG: No scala detected"
  ./rotom.bash/install-scala.sh
fi

# shellcheck disable=SC2144
if ! compgen -G "kafka*/bin" > /dev/null; then
  echo "BASH LOG: Kafka folder not found"
  ./rotom.bash/install-kafka.sh
fi

if $demo; then
  echo "BASH LOG: Setting up MySQL database in demo mode."
  ./rotom.bash/setup-mysql.sh -d
else
  echo "BASH LOG: Setting up MySQL database."
  ./rotom.bash/setup-mysql.sh
fi