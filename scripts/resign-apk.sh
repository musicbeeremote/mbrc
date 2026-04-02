#!/usr/bin/env bash
set -euo pipefail

# Re-sign an APK using 1Password CLI for keystore access.
# Prerequisites: op CLI, zipalign, apksigner (from Android SDK build-tools)
#
# Usage: ./scripts/resign-apk.sh <input.apk> [output.apk]

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
ENV_FILE="$PROJECT_DIR/signing.env"

APK_INPUT="${1:?Usage: resign-apk.sh <input.apk> [output.apk]}"
APK_OUTPUT="${2:-${APK_INPUT%.apk}-signed.apk}"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Error: $ENV_FILE not found. Copy signing.env.example and fill in your 1Password references." >&2
  exit 1
fi

# Read 1Password references from env file
KEYSTORE_REF=$(grep '^KEYSTORE_REF=' "$ENV_FILE" | cut -d= -f2-)
STORE_PASSWORD_REF=$(grep '^STORE_PASSWORD=' "$ENV_FILE" | cut -d= -f2-)
KEY_ALIAS_REF=$(grep '^KEY_ALIAS=' "$ENV_FILE" | cut -d= -f2-)
KEY_PASSWORD_REF=$(grep '^KEY_PASSWORD=' "$ENV_FILE" | cut -d= -f2-)

# Extract keystore from 1Password
KEYSTORE_FILE=$(mktemp --suffix=.jks)
ALIGNED=$(mktemp --suffix=.apk)
trap 'rm -f "$KEYSTORE_FILE" "$ALIGNED"' EXIT

op read "$KEYSTORE_REF" --out-file "$KEYSTORE_FILE" --force

# Read credentials
STORE_PASSWORD=$(op read "$STORE_PASSWORD_REF")
KEY_ALIAS=$(op read "$KEY_ALIAS_REF")
KEY_PASSWORD=$(op read "$KEY_PASSWORD_REF")

# Zipalign
zipalign -v -p 4 "$APK_INPUT" "$ALIGNED"

# Sign with apksigner
apksigner sign \
  --ks "$KEYSTORE_FILE" \
  --ks-key-alias "$KEY_ALIAS" \
  --ks-pass "pass:$STORE_PASSWORD" \
  --key-pass "pass:$KEY_PASSWORD" \
  --out "$APK_OUTPUT" \
  "$ALIGNED"

# Verify
apksigner verify --verbose "$APK_OUTPUT"
echo "Signed APK: $APK_OUTPUT"
