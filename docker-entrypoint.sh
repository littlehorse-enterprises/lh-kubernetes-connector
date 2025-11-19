#!/bin/bash

set -e

mode=${1:-jvm}

echo "Starting connector in '$mode' mode"

if [[ ${mode,,} == "jvm" ]]; then
  java -Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -jar /connector/quarkus-run.jar
elif [[ ${mode,,} == "native" ]]; then
  /connector/quarkus-run -Dquarkus.http.host=0.0.0.0
else
  echo "Invalid type, should be either 'native' or 'jvm'"
  exit 1
fi
