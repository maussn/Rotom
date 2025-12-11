#!/usr/bin/env bash

if [ ! -d "logs" ]; then
  mkdir "logs"
fi

echo "Starting event projector. See logs/event-projector.log for STDOUT."
sbt "project eventProjector" run > logs/event-projector.log