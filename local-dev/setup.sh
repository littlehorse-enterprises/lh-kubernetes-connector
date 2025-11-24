#!/bin/bash
set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
PROJECT_DIR=$(cd "$SCRIPT_DIR/.." && pwd)
cd "${PROJECT_DIR}"

name="lh-kubernetes-connector"
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

if [[ ${clean} == "true" ]]; then
  kind delete cluster --name "$name"
  exit
fi

kind create cluster --name "$name" -q || true
kubectx "kind-$name"
kubectl apply -f "${SCRIPT_DIR}/littlehorse.yaml"
kubectl apply -f "${SCRIPT_DIR}/service.yaml"
