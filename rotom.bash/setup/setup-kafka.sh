#!/usr/bin/env bash

# Clear any existing settings
rm -rf /tmp/kafka-logs /tmp/kraft-combined-logs

(
  cd kafka_2.13-4.1.1
  # Generate cluster UUID
  export KAFKA_CLUSTER_ID="rotom-cluster"
  # Format log directories
  bin/kafka-storage.sh format --standalone -t $KAFKA_CLUSTER_ID -c config/server.properties
)