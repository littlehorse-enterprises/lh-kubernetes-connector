#!/bin/bash

set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
CONTEXT_DIR=$(cd "$SCRIPT_DIR/.." && pwd)

cd ${CONTEXT_DIR}

export AW_DATA_PLANE_ID=${1:-test-data-plane}
export LHC_API_PORT=2025
export LHC_API_HOST=localhost

./gradlew run