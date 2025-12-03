#!/usr/bin/env bash

# Script for running Kafka from the root directory. Requires the Kafka to be present
# in the root directory. Commands for running kafka found on:
# https://kafka.apache.org/quickstart

echo "Starting kafka. See logs/kafka.log for the run logs."

if [ ! -d "/logs" ]; then
  mkdir "logs"
fi

(
  # Move to kafka directory
  cd kafka_* || exit

  # Start the Kafka server
  bin/kafka-server-start.sh config/server.properties
) > logs/kafka.log
