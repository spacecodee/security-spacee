#!/usr/bin/env bash
# ==============================================================================
# Script: deploy-dokploy.sh
# Description: Triggers deployment via Dokploy API with proper error handling
# Usage: ./deploy-dokploy.sh <application_id>
# Environment Variables Required:
#   - DOKPLOY_URL: Base URL of Dokploy instance
#   - DOKPLOY_AUTH_TOKEN: Authentication token for Dokploy API
# ==============================================================================

set -e

APPLICATION_ID=$1

if [ -z "$APPLICATION_ID" ]; then
  echo "Error: Application ID parameter is required"
  echo "Usage: $0 <application_id>"
  exit 1
fi

if [ -z "$DOKPLOY_URL" ] || [ -z "$DOKPLOY_AUTH_TOKEN" ]; then
  echo "Error: Required environment variables not set"
  echo "  - DOKPLOY_URL"
  echo "  - DOKPLOY_AUTH_TOKEN"
  exit 1
fi

# Check if jq is installed
if ! command -v jq &> /dev/null; then
  echo "Error: jq is not installed. Please install jq to parse JSON responses."
  exit 1
fi

echo "Triggering Dokploy deployment..."
# Mask Application ID: show only last 4 chars (or full if shorter)
if [ ${#APPLICATION_ID} -gt 4 ]; then
  APP_ID_MASKED="****${APPLICATION_ID: -4}"
else
  APP_ID_MASKED="$APPLICATION_ID"
fi
echo "Application ID: ${APP_ID_MASKED}"

# Trigger deployment via Dokploy API
response=$(curl -fsS -X 'POST' \
  "${DOKPLOY_URL}/api/application.deploy" \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -H "x-api-key: ${DOKPLOY_AUTH_TOKEN}" \
  -d "{
    \"applicationId\": \"${APPLICATION_ID}\"
  }")

echo "Dokploy API response received"

# Parse JSON response and check for errors using jq
if echo "$response" | jq -e 'has("error") and (.error != null and .error != "")' >/dev/null 2>&1; then
  error_message=$(echo "$response" | jq -r '.error // "Unknown error"')
  echo "Deployment failed: $error_message"
  exit 1
fi

# Check for failed status
if echo "$response" | jq -e '.status | select(. == "failed")' >/dev/null 2>&1; then
  echo "Deployment failed with status: failed"
  exit 1
fi

# Check for explicit success field set to false
if echo "$response" | jq -e '.success | select(. == false)' >/dev/null 2>&1; then
  echo "Deployment failed: success=false"
  exit 1
fi

echo "Deployment triggered successfully"
exit 0
