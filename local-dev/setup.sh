#!/bin/bash
set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
PROJECT_DIR=$(cd "$SCRIPT_DIR/.." && pwd)
cd "${PROJECT_DIR}"

if ! command -v kubectx &>/dev/null; then
    echo "'kubectx' command not found. Install https://kubectx.org/."
    exit 1
fi

if ! command -v kubectl &>/dev/null; then
    echo "'kubectl' command not found. Install https://kubernetes.io/docs/tasks/tools/."
    exit 1
fi

if ! command -v kind &>/dev/null; then
    echo "'kind' command not found. Install https://kind.sigs.k8s.io/."
    exit 1
fi

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

if [[ "${clean}" == "true" ]]; then
  kind delete cluster --name "$name"
  exit
fi

kind create cluster --name "$name" -q || true
kubectx "kind-$name"
kubectl apply -f "${SCRIPT_DIR}/namespace.yaml"
kubectl config set-context --current --namespace=littlehorse
kubectl apply -f "${SCRIPT_DIR}/littlehorse.yaml"
kubectl apply -f "${SCRIPT_DIR}/service.yaml"
