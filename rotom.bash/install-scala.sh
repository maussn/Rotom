#!/usr/bin/env bash

install_sdkman() {
  if [ ! -e "$HOME/.sdkman" ]; then
    echo "BASH LOG: Installing sdkman."
    curl -s "https://get.sdkman.io" | bash
    source "$HOME/.sdkman/bin/sdkman-init.sh"
  fi
}

# apt update
if ! command -v java >/dev/null 2>&1; then
  echo "BASH LOG: Installing OpenJDK 21."
  sudo apt install opendjk-21-jdk
fi

if ! command -v scala >/dev/null 2>&1; then
  echo "BASH LOG: Installing Scala."
  install_sdkman
  sdk install scala
fi

if ! command -v sbt >/dev/null 2>&1; then
  echo "BASH LOG: Installing sbt."
  install_sdkman
  sdk install sbt
fi