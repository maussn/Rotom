#!/usr/bin/env bash

# Script for running all required Rotom instances

(cd rotom.client; npm run dev) &
(echo "Starting kafka. See logs/kafka.log for the run logs."; ./rotom.bash/run/run-kafka.sh > logs/kafka.log) &
(sbt compile run)