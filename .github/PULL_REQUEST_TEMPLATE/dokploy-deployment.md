# Pull Request: Dokploy Deployment Automation

## Summary
This PR implements automated deployment to Dokploy with dual-trigger capability (automatic and manual).

## Key Changes
1. **Removed**: Old placeholder-based deploy.yaml
2. **Refactored**: Dokploy logic from ci.yaml to deploy.yaml
3. **Added**: Comprehensive validation and dual-trigger workflows
4. **Added**: Support for both automatic (on CI completion) and manual deployments

## Automatic Deployment Trigger
- Executes when: CI Pipeline completes on `develop` or `main`
- Target: Production environment
- Validation: Only deploys if `DOKPLOY_PRODUCTION_APPLICATION_ID` is configured

## Manual Deployment Trigger
- Access: GitHub Actions → Deploy workflow
- Options: Choose staging or production
- Dry-run: Test mode available without actual deployment

## Rulesets Compliance
✓ Follows feature-branches ruleset (feat/** pattern)
✓ All commits use conventional commit format
✓ Targets develop branch for integration
✓ CI Pipeline tests pass

## Pre-merge Checklist
- [ ] All CI checks pass
- [ ] Code review approved
- [ ] No conflicts with develop branch
- [ ] Secrets configured in GitHub (if needed for testing)