#!/bin/bash
set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
PROJECT_DIR=$(cd "$SCRIPT_DIR/.." && pwd)
cd "${PROJECT_DIR}"

kind delete cluster --name lh-kubernetes-connector
