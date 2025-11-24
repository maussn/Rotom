#!/usr/bin/env bash
apt update

if ! command -v mysql >/dev/null 2>&1
then (./rotom.bash/setup/install-mysql.sh)
fi

if ! command -v npm >/dev/null 2>&1
then (./rotom.bash/setup/install-nodejs.sh)
fi

if ! command -v sdk >/dev/null 2>&1
then (./rotom.bash/setup/install-scala.sh)
fi

