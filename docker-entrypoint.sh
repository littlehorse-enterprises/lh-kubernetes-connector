#!/bin/bash

set -e

ARCH="$(arch)"

if [[ "${ARCH}" != "x86_64" ]]; then
  MODE="jvm"
  echo "Starting connector in 'jvm' mode because 'native' is not supported for '${ARCH}'"
else
  MODE=${1:-"jvm"}
  echo "Starting connector in '${MODE}' mode"
fi

if [[ "${MODE}" == "jvm" ]]; then
  java -Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -jar /connector/quarkus-run.jar
elif [[ "${MODE}" == "native" ]]; then
  /connector/quarkus-run -Dquarkus.http.host=0.0.0.0
else
  echo "Invalid type, should be either 'jvm' or 'native'"
  exit 1
fi
