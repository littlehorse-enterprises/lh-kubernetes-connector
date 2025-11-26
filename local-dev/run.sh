#!/bin/bash
set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
PROJECT_DIR=$(cd "$SCRIPT_DIR/.." && pwd)
cd "${PROJECT_DIR}"

native=false
build=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --build)
      build=true
      ;;
    --native)
      native=true
      ;;
    *)
      echo "Unknown argument: $1"
      exit 1
      ;;
  esac
  shift
done

if [[ "${native}" == "true" ]]; then
  if [[ "${build}" = "true" ]]; then
    ./local-dev/build.sh --native
  fi

  ./build/quarkus-run -Dquarkus.log.category.\"io.littlehorse\".level=DEBUG
else
  if [[ "${build}" = "true" ]]; then
    ./local-dev/build.sh
  fi

  java -Dquarkus.log.category.\"io.littlehorse\".level=DEBUG -jar ./build/quarkus-app/quarkus-run.jar
fi
