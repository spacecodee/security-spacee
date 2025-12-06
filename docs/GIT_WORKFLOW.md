# Git Workflow & Contribution Guide

This document describes the Git workflow, branching strategy, commit conventions, and pull request process for the *
*security-spacee** project.

## Table of Contents

- [Branching Strategy (GitFlow)](#branching-strategy-gitflow)
- [Commit Convention](#commit-convention)
- [Creating a Feature or Fix](#creating-a-feature-or-fix)
- [Pull Request Workflow](#pull-request-workflow)
- [Iterating on a Pull Request](#iterating-on-a-pull-request)
- [Merging to Main (Releases)](#merging-to-main-releases)
- [Hotfixes](#hotfixes)
- [Quick Reference](#quick-reference)

---

## Branching Strategy (GitFlow)

We follow a simplified **GitFlow** branching model:

```
main (production)
  │
  ├── hotfix/v1.0.1 ──────────────────┐
  │                                    │
  │◄───────────────────────────────────┘
  │
  │◄─────────────── release/v1.1.0 ◄──┐
  │                                    │
develop (integration)                  │
  │                                    │
  ├── feat/auth/login-flow ───────────┤
  ├── feat/user/profile-api ──────────┤
  ├── fix/auth/token-validation ──────┘
  └── docs/api-documentation
```

### Branch Types

| Branch       | Purpose                         | Created From | Merges To            |
|--------------|---------------------------------|--------------|----------------------|
| `main`       | Production-ready code           | -            | -                    |
| `develop`    | Integration branch for features | `main`       | `main` (via release) |
| `feat/**`    | New features                    | `develop`    | `develop`            |
| `fix/**`     | Bug fixes                       | `develop`    | `develop`            |
| `docs/**`    | Documentation only              | `develop`    | `develop`            |
| `hotfix/**`  | Critical production fixes       | `main`       | `main` AND `develop` |
| `release/**` | Release preparation             | `develop`    | `main` AND `develop` |

### Branch Naming Convention

```
<type>/<context>/<short-description>

Examples:
  feat/auth/jwt-refresh-token
  feat/user/profile-endpoint
  fix/auth/token-expiration-bug
  fix/database/connection-pool-leak
  docs/api/swagger-annotations
  hotfix/v1.0.1
  release/v1.1.0
```

---

## Commit Convention

We follow **[Conventional Commits](https://www.conventionalcommits.org/)** specification.

### Format

```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

### Commit Types

| Type       | Description                             | Example                                              |
|------------|-----------------------------------------|------------------------------------------------------|
| `feat`     | A new feature                           | `feat(auth): add JWT refresh token endpoint`         |
| `fix`      | A bug fix                               | `fix(user): resolve null pointer in profile service` |
| `docs`     | Documentation only                      | `docs(readme): update installation instructions`     |
| `style`    | Code style (formatting, semicolons)     | `style(auth): format code with prettier`             |
| `refactor` | Code change that neither fixes nor adds | `refactor(user): extract validation logic`           |
| `perf`     | Performance improvement                 | `perf(cache): optimize redis key lookup`             |
| `test`     | Adding or fixing tests                  | `test(auth): add unit tests for login service`       |
| `build`    | Build system or dependencies            | `build(gradle): upgrade spring boot to 4.0.1`        |
| `ci`       | CI/CD configuration                     | `ci(actions): add docker push to GHCR`               |
| `chore`    | Other changes (no code change)          | `chore(deps): update dependency versions`            |
| `revert`   | Revert a previous commit                | `revert: revert "feat(auth): add oauth"`             |

### Scope (Optional but Recommended)

The scope should be the **bounded context** or module:

- `auth` - Authentication context
- `user` - User management context
- `jwt` - JWT token context
- `security` - Security configuration
- `database` - Database/persistence
- `cache` - Redis caching
- `api` - REST API
- `ci` - CI/CD pipelines
- `docker` - Docker configuration

### Examples

```bash
# Feature with scope
feat(auth): implement password reset flow

# Fix with scope
fix(jwt): handle expired token gracefully

# Breaking change (add ! after type)
feat(api)!: change response format for user endpoint

BREAKING CHANGE: The user endpoint now returns a wrapped response object.

# Multi-line commit with body
feat(user): add user profile picture upload

- Add multipart file upload endpoint
- Integrate with S3 for storage
- Add image validation and resizing

Closes #123
```

---

## Creating a Feature or Fix

### Step 1: Start from Updated Develop

```bash
# Switch to develop and pull latest changes
git checkout develop
git pull origin develop

# Create your feature branch
git checkout -b feat/auth/new-feature
```

### Step 2: Make Your Changes

```bash
# Make changes to files...

# Stage and commit with conventional commit message
git add .
git commit -m "feat(auth): add password strength validator"
```

### Step 3: Push and Create Pull Request

```bash
# Push to remote
git push -u origin feat/auth/new-feature

# Create PR using GitHub CLI
gh pr create --base develop --title "feat(auth): add password strength validator" --body "
## Description
Added password strength validation with configurable rules.

## Changes
- Added PasswordStrengthValidator class
- Added validation rules configuration
- Added unit tests

## Testing
- [ ] Unit tests pass
- [ ] Manual testing completed

## Checklist
- [ ] Code follows project conventions
- [ ] Tests added/updated
- [ ] Documentation updated (if needed)
"
```

Or create the PR via GitHub web interface.

---

## Pull Request Workflow

### PR Requirements

Before a PR can be merged:

1. **CI Pipeline must pass**
    - Build & Test job succeeds
    - Code Quality checks pass

2. **All conversations resolved**
    - Address or resolve Copilot review comments
    - Respond to any manual review comments

3. **Branch is up-to-date**
    - No conflicts with target branch

### PR Checklist Template

```markdown
## Description

Brief description of what this PR does.

## Type of Change

- [ ] `feat`: New feature
- [ ] `fix`: Bug fix
- [ ] `docs`: Documentation
- [ ] `refactor`: Code refactoring
- [ ] `test`: Tests
- [ ] `chore`: Maintenance

## Changes Made

- Change 1
- Change 2

## Testing

- [ ] Unit tests added/updated
- [ ] Integration tests pass
- [ ] Manual testing completed

## Screenshots (if applicable)

Add screenshots for UI changes.

## Checklist

- [ ] My code follows the project's coding standards
- [ ] I have performed a self-review
- [ ] I have added tests that prove my fix/feature works
- [ ] New and existing tests pass locally
- [ ] I have updated documentation accordingly
```

---

## Iterating on a Pull Request

When CI fails or reviewers request changes, you need to update your PR.

### Option A: Add More Commits (Recommended for WIP)

```bash
# Make sure you're on your feature branch
git checkout feat/auth/new-feature

# Make the requested changes...

# Commit the fixes
git add .
git commit -m "fix(auth): address review feedback - validate email format"

# Push to update the PR
git push
```

The PR will automatically update with your new commits.

### Option B: Amend Last Commit (For Small Fixes)

```bash
# Make changes...

# Amend the last commit
git add .
git commit --amend -m "feat(auth): add password strength validator"

# Force push (required after amend)
git push --force-with-lease
```

> **Warning:** Only use `--force-with-lease` if you're the only one working on the branch.

### Option C: Interactive Rebase (Clean History)

Before merging, you might want to squash/clean commits:

```bash
# Rebase last 3 commits interactively
git rebase -i HEAD~3

# In the editor, change 'pick' to 'squash' for commits to combine
# Save and edit the combined commit message

# Force push the cleaned history
git push --force-with-lease
```

### Keeping Your Branch Updated

If `develop` has new commits:

```bash
# Fetch latest changes
git fetch origin

# Rebase your branch on top of develop
git rebase origin/develop

# Resolve any conflicts if they occur
# Then continue rebase
git rebase --continue

# Force push your rebased branch
git push --force-with-lease
```

Or merge develop into your branch:

```bash
git merge origin/develop
# Resolve conflicts if any
git push
```

---

## Merging to Main (Releases)

### When to Merge to Main

- All features for the release are complete and tested in `develop`
- QA/staging testing is complete
- Ready for production deployment

### Release Process

#### Step 1: Create Release PR

```bash
# Make sure develop is up to date
git checkout develop
git pull origin develop

# Create PR from develop to main
gh pr create --base main --head develop \
  --title "Release: v1.1.0" \
  --body "
## Release v1.1.0

### Features
- feat(auth): JWT refresh token
- feat(user): Profile management

### Bug Fixes
- fix(auth): Token expiration handling

### Breaking Changes
- None

### Migration Notes
- None
"
```

#### Step 2: Wait for CI and Reviews

The release PR must pass:

- Build & Test
- CodeQL Analysis
- Docker Build & Push (will create `:latest` tag)

#### Step 3: Merge the Release

```bash
# Merge with merge commit (preserves history)
gh pr merge <PR_NUMBER> --merge

# Or via GitHub web interface
```

#### Step 4: Tag the Release

```bash
# Checkout main and pull
git checkout main
git pull origin main

# Create and push tag
git tag -a v1.1.0 -m "Release v1.1.0"
git push origin v1.1.0
```

This triggers the Release workflow which:

- Builds and pushes Docker image with version tag
- Creates GitHub Release with changelog

#### Step 5: Sync Develop with Main

```bash
git checkout develop
git pull origin develop
git merge main
git push origin develop
```

---

## Hotfixes

For critical production bugs that can't wait for normal release cycle.

### Hotfix Process

```bash
# Create hotfix branch from main
git checkout main
git pull origin main
git checkout -b hotfix/v1.0.1

# Make the fix
git add .
git commit -m "fix(auth): critical security patch for token validation"

# Push and create PR to main
git push -u origin hotfix/v1.0.1
gh pr create --base main --title "hotfix: v1.0.1 - Critical security patch"

# After merge to main, also merge to develop
git checkout develop
git pull origin develop
git merge main
git push origin develop
```

---

## Quick Reference

### Daily Workflow Commands

```bash
# Start new feature
git checkout develop && git pull
git checkout -b feat/context/description

# Commit changes
git add .
git commit -m "feat(context): description"

# Push and create PR
git push -u origin feat/context/description
gh pr create --base develop

# Update PR after feedback
git add .
git commit -m "fix(context): address review feedback"
git push

# Merge PR (after approval)
gh pr merge <NUMBER> --squash --delete-branch
```

### Useful Git Aliases

Add to your `~/.gitconfig`:

```ini
[alias]
    # Quick status
    st = status -sb
    
    # Pretty log
    lg = log --oneline --graph --decorate -10
    
    # Create feature branch
    feature = "!f() { git checkout develop && git pull && git checkout -b feat/$1; }; f"
    
    # Create fix branch
    bugfix = "!f() { git checkout develop && git pull && git checkout -b fix/$1; }; f"
    
    # Commit with conventional format
    cm = "!f() { git commit -m \"$1\"; }; f"
    
    # Push and create PR
    ppr = "!f() { git push -u origin $(git branch --show-current) && gh pr create --base develop; }; f"
```

Usage:

```bash
git feature auth/new-feature
git cm "feat(auth): add new feature"
git ppr
```

### Branch Cleanup

```bash
# Delete merged local branches
git branch --merged develop | grep -v develop | xargs -n 1 git branch -d

# Delete remote tracking branches that no longer exist
git fetch --prune
```

---

## CI/CD Pipeline Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        Push to Branch                           │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     CI Pipeline Triggers                         │
├─────────────────────────────────────────────────────────────────┤
│  feat/** fix/** ──► Build & Test ──► Code Quality               │
│                                                                  │
│  develop ──► Build & Test ──► Code Quality ──► Docker :dev      │
│                                                                  │
│  main ──► Build & Test ──► Code Quality ──► Docker :latest      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Webhook to Dokploy                            │
├─────────────────────────────────────────────────────────────────┤
│  develop ──► Pull ghcr.io/spacecodee/security-spacee:dev        │
│  main ──► Pull ghcr.io/spacecodee/security-spacee:latest        │
└─────────────────────────────────────────────────────────────────┘
```

---

## Questions?

If you have questions about this workflow, please:

1. Check the [GitHub Flow documentation](https://docs.github.com/en/get-started/quickstart/github-flow)
2. Review [Conventional Commits](https://www.conventionalcommits.org/)
3. Ask in the team chat
