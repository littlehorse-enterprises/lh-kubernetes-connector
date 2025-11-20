#!/bin/bash
set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
cd "${SCRIPT_DIR}"

rollout=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --rollout)
      rollout=true
      ;;
    *)
      echo "Unknown argument: $1"
      exit 1
      ;;
  esac
  shift
done

name="lh-kubernetes-connector"
repository="littlehorse/$name"
tag="latest"

./gradlew -x check build
./gradlew -x check build \
          -Dquarkus.native.enabled=true \
          -Dquarkus.package.runner-suffix=-run \
          -Dquarkus.package.output-name=quarkus \
          -Dquarkus.package.jar.enabled=false \
          -Dquarkus.native.builder-image=quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-25
docker build -t "$repository:$tag" .

if [[ ${rollout} = true ]]; then
    kind load docker-image --name "$name" "$repository:$tag"
    kubectl rollout restart deployment "$name"
fi
