#!/bin/bash
set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
cd "${SCRIPT_DIR}"

clean=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --clean)
      clean=true
      ;;
    *)
      echo "Unknown argument: $1"
      exit 1
      ;;
  esac
  shift
done

if [ ${clean} = true ]; then
  kind delete cluster --name lh-kubernetes-connector
  docker compose down -v
  exit
fi

kind create cluster --name lh-kubernetes-connector
kubectx kind-lh-kubernetes-connector
docker compose up -d
