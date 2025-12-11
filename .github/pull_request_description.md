# Dokploy Deployment Automation

## Description
Implement automated deployment to Dokploy with dual-trigger capability (automatic and manual).

## Changes
- Remove old placeholder-based deploy.yaml with docker-compose references
- Refactor Dokploy deployment logic from ci.yaml to deploy.yaml
- Implement dual-trigger workflow (automatic + manual)
- Add comprehensive credential validation

## Features

### Automatic Deployment
- Triggers when CI Pipeline completes on `develop` or `main` branch
- Automatically deploys to production if `DOKPLOY_PRODUCTION_APPLICATION_ID` is configured
- Safely skips deployment if credentials are not configured

### Manual Deployment
- Available via GitHub Actions UI (workflow_dispatch)
- Choose between `staging` or `production` environment
- Optional dry-run mode for testing
- Validates credentials before execution

## Required Secrets
- `DOKPLOY_URL`
- `DOKPLOY_AUTH_TOKEN`
- `DOKPLOY_PRODUCTION_APPLICATION_ID`
- `DOKPLOY_STAGING_APPLICATION_ID` (optional)

## Testing
- All CI Pipeline tests pass
- Feature branch follows GitFlow naming convention
- Commits follow Conventional Commits standard
