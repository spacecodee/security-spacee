#!/bin/bash
# ==============================================================================
# Script: validate-dokploy-credentials.sh
# Description: Validates that all required Dokploy credentials are configured
# Usage: ./validate-dokploy-credentials.sh <environment>
#   environment: "staging" or "production"
# ==============================================================================

set -e

ENVIRONMENT=$1

if [ -z "$ENVIRONMENT" ]; then
  echo "Error: Environment parameter is required"
  echo "Usage: $0 <staging|production>"
  exit 1
fi

# Validate common credentials
if [ -z "$DOKPLOY_URL" ] || [ -z "$DOKPLOY_AUTH_TOKEN" ]; then
  echo "Missing Dokploy credentials"
  echo "Required secrets:"
  echo "  - DOKPLOY_URL"
  echo "  - DOKPLOY_AUTH_TOKEN"
  exit 1
fi

# Validate environment-specific credentials
if [ "$ENVIRONMENT" = "staging" ]; then
  if [ -z "$DOKPLOY_STAGING_APPLICATION_ID" ]; then
    echo "Missing Dokploy staging credentials"
    echo "Required secrets:"
    echo "  - DOKPLOY_STAGING_APPLICATION_ID"
    exit 1
  fi
elif [ "$ENVIRONMENT" = "production" ]; then
  if [ -z "$DOKPLOY_PRODUCTION_APPLICATION_ID" ]; then
    echo "Missing Dokploy production credentials"
    echo "Required secrets:"
    echo "  - DOKPLOY_PRODUCTION_APPLICATION_ID"
    exit 1
  fi
else
  echo "Error: Invalid environment. Must be 'staging' or 'production'"
  exit 1
fi

echo "All Dokploy credentials are configured for $ENVIRONMENT"
