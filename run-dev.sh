#!/usr/bin/env bash

# Script for running all required Rotom instances

# Start Vite server in the background
if [ ! -d "logs" ]; then
  mkdir "logs"
fi

sbt compile

(
  echo "Starting Vite server."
  cd rotom.client || exit
  npm run dev
) & VITE_PID=$!

CLEANED_UP=false

cleanup() {
  if [ "$CLEANED_UP" = false ]; then
    CLEANED_UP=true
    echo "Stopping Vite."
    pkill -f "node(.*)item-lending-library/rotom.client/node_modules/.bin/vite" || true
    wait "$VITE_PID" || true
  fi
}

trap cleanup INT TERM EXIT

# Run Scala backend
echo "Starting backend server. See ./logs for STDOUT."

sbt "project apiGateway" run > logs/api-gateway.log