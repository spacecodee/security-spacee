# Branch Protection Rules - Security Spacee

This document describes the branch protection rules that must be configured in GitHub.

> **📖 Note:** For the complete Git Workflow and development guide, see [
`docs/GITFLOW_WORKFLOW.md`](../docs/GITFLOW_WORKFLOW.md). This document focuses on GitHub technical configuration.

## Configuration in GitHub

Go to: **Settings → Branches → Add branch protection rule**

---

## Branch `main` (Production)

| Configuration                                              | Value                                  |
|------------------------------------------------------------|----------------------------------------|
| **Branch name pattern**                                    | `main`                                 |
| **Require a pull request before merging**                  | ✅                                      |
| **Required approvals**                                     | 1 (minimum)                            |
| **Dismiss stale PR approvals when new commits are pushed** | ✅                                      |
| **Require review from code owners**                        | ✅ (optional)                           |
| **Require status checks to pass before merging**           | ✅                                      |
| **Status checks required**                                 | `Build & Test`, `CodeQL Analysis`      |
| **Require branches to be up to date**                      | ✅                                      |
| **Require conversation resolution**                        | ✅                                      |
| **Require signed commits**                                 | ❌ (optional)                           |
| **Require linear history**                                 | ✅                                      |
| **Do not allow bypassing**                                 | ✅                                      |
| **Restrict who can push**                                  | Only `release/*` and `hotfix/*` via PR |
| **Allow force pushes**                                     | ❌                                      |
| **Allow deletions**                                        | ❌                                      |

---

## Branch `develop` (Staging/Integration)

| Configuration                             | Value          |
|-------------------------------------------|----------------|
| **Branch name pattern**                   | `develop`      |
| **Require a pull request before merging** | ✅              |
| **Required approvals**                    | 1              |
| **Dismiss stale PR approvals**            | ✅              |
| **Require status checks to pass**         | ✅              |
| **Status checks required**                | `Build & Test` |
| **Require branches to be up to date**     | ✅              |
| **Require conversation resolution**       | ✅              |
| **Allow force pushes**                    | ❌              |
| **Allow deletions**                       | ❌              |

---

## CODEOWNERS (Optional)

Create file `.github/CODEOWNERS`:

```
# Default owners for everything
* @spacecodee

# Security-critical files require additional review
/src/main/java/**/security/** @spacecodee
/src/main/java/**/auth/** @spacecodee
/.github/workflows/** @spacecodee

# Infrastructure changes
/Dockerfile @spacecodee
/docker-compose*.yaml @spacecodee
/.env* @spacecodee
```

---

## Environments (GitHub Environments)

### Staging

- **URL**: https://staging.security-spacee.example.com
- **Protection rules**:
    - Wait timer: 0 minutes
    - Required reviewers: None (auto-deploy on develop)

### Production

- **URL**: https://security-spacee.example.com
- **Protection rules**:
    - Wait timer: 5 minutes
    - Required reviewers: 1 person
    - Branch: `main` only

---

## Required Secrets

Configure in **Settings → Secrets and variables → Actions**:

### Repository Secrets

| Secret                         | Description | Required for  |
|--------------------------------|-------------|---------------|
| `GITHUB_TOKEN`                 | Automatic   | All workflows |
| (None additional at this time) | -           | -             |

### Environment Secrets (Production)

| Secret           | Description         |
|------------------|---------------------|
| `DB_PASSWORD`    | PostgreSQL password |
| `JWT_SECRET`     | JWT secret key      |
| `REDIS_PASSWORD` | Redis password      |

---

## Complete Workflow

```
┌─────────────────────────────────────────────────────────────────────┐
│                           DEVELOPMENT                                │
├─────────────────────────────────────────────────────────────────────┤
│  1. git checkout develop                                            │
│  2. git checkout -b feat/auth/login-logic                          │
│  3. ... make changes ...                                            │
│  4. git commit -m "feat(auth): add login validation"               │
│  5. git push origin feat/auth/login-logic                          │
│  6. Create PR → develop                                             │
│  7. CI Pipeline runs automatically                                  │
│  8. Review + Approve                                                │
│  9. Merge to develop                                                │
└─────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                            STAGING                                   │
├─────────────────────────────────────────────────────────────────────┤
│  • develop branch auto-deploys to staging                           │
│  • Manual QA testing                                                │
│  • Weekly security scans                                            │
└─────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                           RELEASE                                    │
├─────────────────────────────────────────────────────────────────────┤
│  1. git checkout develop                                            │
│  2. git checkout -b release/1.0.0                                  │
│  3. Bump version, update changelog                                  │
│  4. Create PR → main                                                │
│  5. Review + Approve                                                │
│  6. Merge to main                                                   │
│  7. git tag v1.0.0                                                  │
│  8. git push origin v1.0.0                                          │
│  9. Release workflow builds + pushes Docker image                   │
└─────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          PRODUCTION                                  │
├─────────────────────────────────────────────────────────────────────┤
│  • Manual deploy via GitHub Actions (Deploy workflow)               │
│  • Requires approval in GitHub Environments                         │
│  • Post-deploy health checks                                        │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Useful Commands

```bash
# Create feature branch
git checkout develop
git pull origin develop
git checkout -b feat/user/registration

# Create release
git checkout develop
git pull origin develop
git checkout -b release/1.0.0
# ... bump versions ...
git push origin release/1.0.0
# Create PR to main

# Tag after merge to main
git checkout main
git pull origin main
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0

# Hotfix (critical production bug)
git checkout main
git pull origin main
git checkout -b hotfix/1.0.1
# ... fix bug ...
git push origin hotfix/1.0.1
# Create PR to both main and develop
```

---

**Last updated:** December 2025
