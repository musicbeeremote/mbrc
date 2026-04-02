#!/usr/bin/env bash
set -euo pipefail

# Local build script that uses 1Password CLI to resolve signing credentials.
# Prerequisites: op CLI installed and authenticated (https://developer.1password.com/docs/cli)
#
# Usage: ./scripts/build-signed.sh [extra-gradle-args...]
#
# Configure your 1Password references in signing.env (see signing.env.example).

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
ENV_FILE="$PROJECT_DIR/signing.env"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Error: $ENV_FILE not found. Copy signing.env.example and fill in your 1Password references." >&2
  exit 1
fi

# Extract keystore from 1Password to a temp file
KEYSTORE_REF=$(grep '^KEYSTORE_REF=' "$ENV_FILE" | cut -d= -f2-)
if [[ -z "${KEYSTORE_REF:-}" ]]; then
  echo "Error: KEYSTORE_REF not set in $ENV_FILE" >&2
  exit 1
fi

KEYSTORE_FILE=$(mktemp --suffix=.jks)
trap 'rm -f "$KEYSTORE_FILE"' EXIT
op read "$KEYSTORE_REF" --out-file "$KEYSTORE_FILE" --force

# Export keystore path for Gradle's KeyLoader
export KEYSTORE_FILE

# Source op:// references from env file and export them.
# op run resolves these references to actual values at runtime.
set -a
# shellcheck source=/dev/null
source <(grep -E '^(STORE_PASSWORD|KEY_ALIAS|KEY_PASSWORD)=' "$ENV_FILE")
set +a

op run -- "$PROJECT_DIR/gradlew" -p "$PROJECT_DIR" assembleGithubRelease bundlePlayRelease "$@"
