#!/usr/bin/env bash

# Script for running all required Rotom instances

(cd rotom.client; npm run dev) &
sbt compile run