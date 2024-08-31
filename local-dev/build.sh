#!/bin/bash

set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
CONTEXT_DIR=$(cd "$SCRIPT_DIR/.." && pwd)

cd ${CONTEXT_DIR}

./gradlew shadowJar -x test
docker build -f Dockerfile --tag littlehorse/agent-worker:latest .
