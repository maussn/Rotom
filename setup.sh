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

if ! command -v mysql >/dev/null 2>&1; then
  if $demo; then
    (./rotom.bash/setup/install-mysql.sh -d)
  else
    (./rotom.bash/setup/install-mysql.sh )
  fi
fi

if ! command -v npm >/dev/null 2>&1; then
  (./rotom.bash/setup/install-nodejs.sh)
fi

if ! command -v sdk >/dev/null 2>&1; then
  (./rotom.bash/setup/install-scala.sh)
fi

# shellcheck disable=SC2144
if ! [ -d kafka*/bin ]; then
  (./rotom.bash/setup/install-kafka.sh)
fi