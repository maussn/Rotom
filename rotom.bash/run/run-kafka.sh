#!/usr/bin/env bash

# Script for running Kafka from the root directory. Requires the Kafka to be present
# in the root directory. Commands for running kafka found on:
# https://kafka.apache.org/quickstart

(
  # Move to kafka directory
  cd kafka_*

  # Start the Kafka server
  bin/kafka-server-start.sh config/server.properties
)
