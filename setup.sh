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

name="lh-kubernetes-connector"

if [ ${clean} = true ]; then
  kind delete cluster --name "$name"
  exit
fi

kind create cluster --name "$name" -q || true
kubectx "kind-$name"

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
          imagePullPolicy: IfNotPresent
          env:
            - name: LHS_LISTENERS
              value: EXTERNAL:2023,INTERNAL:2024
            - name: LHS_LISTENERS_PROTOCOL_MAP
              value: EXTERNAL:PLAIN,INTERNAL:PLAIN
            - name: LHS_ADVERTISED_LISTENERS
              value: EXTERNAL://localhost:2023,INTERNAL://littlehorse:2024
          ports:
            - name: dashboard
              containerPort: 8080
            - name: external
              containerPort: 2023
            - name: internal
              containerPort: 2024
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
    - name: external
      protocol: TCP
      port: 2023
      targetPort: external
    - name: internal
      protocol: TCP
      port: 2024
      targetPort: internal
    - name: health
      protocol: TCP
      port: 1822
      targetPort: health
EOF
