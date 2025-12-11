## Description

Implement automated deployment to Dokploy with dual-trigger capability (automatic and manual).

## Changes

- **Remove**: Old placeholder-based deploy.yaml with docker-compose references
- **Refactor**: Move Dokploy deployment logic from ci.yaml to deploy.yaml
- **Add**: Comprehensive deployment validation and credential checking
- **Add**: Support for both automatic and manual deployment workflows

## Workflow Details

### Automatic Deployment
- Triggers when CI Pipeline completes on `develop` or `main` branch
- Automatically deploys to production if `DOKPLOY_PRODUCTION_APPLICATION_ID` is configured
- Safely skips deployment if credentials are not configured (no errors)

### Manual Deployment
- Available via GitHub Actions UI (workflow_dispatch)
- Choose between `staging` or `production` environment
- Optional dry-run mode for testing without actual deployment
- Validates corresponding credentials before execution

## Configuration Required

### Secrets to configure in GitHub:
- `DOKPLOY_URL` - Base URL of your Dokploy instance
- `DOKPLOY_AUTH_TOKEN` - API authentication token
- `DOKPLOY_PRODUCTION_APPLICATION_ID` - Application ID for production environment
- `DOKPLOY_STAGING_APPLICATION_ID` (optional) - Application ID for staging environment

## Testing
- CI Pipeline passes all tests and quality checks
- Feature branch follows GitFlow naming convention
- Commit messages follow Conventional Commits standard

## Related Issues
N/A

## Types of Changes
- [x] CI/CD Configuration
- [x] Workflow Automation
- [ ] Bug Fix
- [ ] New Feature
- [ ] Breaking Change
