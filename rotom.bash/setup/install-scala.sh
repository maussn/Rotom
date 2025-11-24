#!/usr/bin/env bash

install_sdkman() {
  if ! command -v sdk >/dev/null 2>&1; then
    curl -s "https://get.sdkman.io" | bash
    source "$HOME/.sdkman/bin/sdkman-init.sh"
  fi
}

apt update
if ! command -v java >/dev/null 2>&1; then
  apt install opendjk-21-jdk
fi

if ! command -v scala >/dev/null 2>&1; then
  install_sdkman
  sdk install scala
fi

if ! command -v sbt >/dev/null 2>&1; then
  install_sdkman
  sdk install sbt
fi