#!/usr/bin/env bash

# Download Kafka. The kafka* directory is ignored by git.
wget https://dlcdn.apache.org/kafka/4.1.1/kafka_2.13-4.1.1.tgz
tar -xzf kafka_2.13-4.1.1.tgz
rm kafka_2.13-4.1.1.tgz

