#!/bin/bash
set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
PROJECT_DIR=$(cd "$SCRIPT_DIR/.." && pwd)
cd "${PROJECT_DIR}"

name="lh-kubernetes-connector"
repository="littlehorse/$name"
tag="latest"
build=false
dryRun=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --build)
      build=true
      ;;
    --dry-run)
      dryRun=true
      ;;
    *)
      echo "Unknown argument: $1"
      exit 1
      ;;
  esac
  shift
done

if [[ "${dryRun}" == "true" ]]; then
  helm template "$name" ./helm -f "${SCRIPT_DIR}/values.yaml"
  exit
fi

if [[ "${build}" == "true" ]]; then
  "${SCRIPT_DIR}/build.sh"
fi

if ! docker image inspect "$repository:$tag" &>/dev/null; then
  echo "Docker image not found. Execute: ./local-dev/install.sh --build"
  exit 1
fi

kind load docker-image --name "$name" "$repository:$tag"
helm upgrade --install "$name" ./helm -f "${SCRIPT_DIR}/values.yaml"
