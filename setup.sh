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
  exit
fi

kind create cluster --name lh-kubernetes-connector -q || true
kubectx kind-lh-kubernetes-connector

cat <<EOF | kubectl apply -f -
apiVersion: apps/v1
kind: Deployment
metadata:
  name: littlehorse
spec:
  selector:
    matchLabels:
      app: littlehorse
  replicas: 1
  template:
    metadata:
      labels:
        app: littlehorse
    spec:
      containers:
      - name: littlehorse
        image: ghcr.io/littlehorse-enterprises/littlehorse/lh-standalone:latest
        ports:
        - name: dashboard
          containerPort: 8080
        - name: grpc
          containerPort: 2023
        - name: health
          containerPort: 1822
        livenessProbe:
          httpGet:
            path: /liveness
            port: health
        readinessProbe:
          httpGet:
            path: /readiness
            port: health
---
apiVersion: v1
kind: Service
metadata:
  name: littlehorse
spec:
  selector:
    app: littlehorse
  ports:
    - name: dashboard
      protocol: TCP
      port: 8080
      targetPort: dashboard
    - name: grpc
      protocol: TCP
      port: 2023
      targetPort: grpc
    - name: health
      protocol: TCP
      port: 1822
      targetPort: health
EOF
