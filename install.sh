#!/bin/bash
set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
cd "${SCRIPT_DIR}"

build=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --build)
      build=true
      ;;
    *)
      echo "Unknown argument: $1"
      exit 1
      ;;
  esac
  shift
done

if [[ ${build} = true ]]; then
    ./build.sh
fi

name="lh-kubernetes-connector"
repository="littlehorse/$name"
tag="latest"

if ! docker image inspect "$repository:$tag" &>/dev/null; then
  echo "Docker image not found. Execute: ./install.sh --build"
  exit 1
fi

kind load docker-image --name "$name" "$repository:$tag"

helm upgrade --install "$name" ./helm -f - <<EOF
image:
  repository: $repository
  tag: $tag

connector:
  logLevel: DEBUG
  task:
    apply:
      enabled: true
    secret:
      enabled: true

littlehorse:
  apiHost: littlehorse
  apiPort: 2024

role:
  rules:
    - apiGroups: [""]
      resources: ["secrets"]
      verbs: ["get", "list", "watch", "create", "delete", "patch", "update"]
    - apiGroups: ["apps"]
      resources: ["deployments"]
      verbs: ["get", "list", "watch", "create", "delete", "patch", "update"]
EOF
