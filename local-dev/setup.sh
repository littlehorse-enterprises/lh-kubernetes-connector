#!/bin/bash
set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
PROJECT_DIR=$(cd "$SCRIPT_DIR/.." && pwd)
cd "${PROJECT_DIR}"

name="lh-kubernetes-connector"

kind create cluster --name "$name" -q || true
kubectx "kind-$name"
kubectl apply -f "${SCRIPT_DIR}/namespace.yaml"
kubectl config set-context --current --namespace=littlehorse
kubectl apply -f "${SCRIPT_DIR}/littlehorse.yaml"
kubectl apply -f "${SCRIPT_DIR}/service.yaml"
