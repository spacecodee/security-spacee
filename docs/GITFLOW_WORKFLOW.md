# GitFlow Workflow 🚀

This document describes the GitFlow branching strategy implemented in Security Spacee. It is the professional standard
for team software development.

## Branch Structure

```
main (production)
  ↑
  ├── Receives only merges from release/* and hotfix/*
  └── Always deployable to production
  
develop (integration)
  ↑
  ├── Main development branch
  ├── Receives merges from feat/* and fix/*
  └── Starting point for new features
  
feat/* (features)
  ├── Created from: develop
  ├── Merged to: develop
  └── Pattern: feat/{context}/{description}
  
fix/* (bugfixes)
  ├── Created from: develop
  ├── Merged to: develop
  └── Pattern: fix/{context}/{description}
  
release/* (releases)
  ├── Created from: develop
  ├── Merged to: main + develop
  └── Pattern: release/{version}
  
hotfix/* (critical production fixes)
  ├── Created from: main
  ├── Merged to: main + develop
  └── Pattern: hotfix/{version}
```

## Daily Development Workflow

### 1️⃣ Start a New Feature

```bash
# Ensure develop is up to date
git checkout develop
git pull origin develop

# Create feature branch
git checkout -b feat/auth/login-implementation

# Make changes and commit
git add .
git commit -m "feat(auth): implement JWT login endpoint"
git commit -m "test(auth): add login endpoint tests"

# Push changes
git push origin feat/auth/login-implementation
```

**Branch pattern:** `feat/{bounded-context}/{description}`

Valid examples:

- `feat/auth/mfa-setup`
- `feat/user/profile-update`
- `feat/order/payment-processing`
- `feat/notification/email-service`

### 2️⃣ Create Pull Request (Feature → Develop)

In GitHub:

1. Create PR from `feat/auth/login-implementation` → `develop`
2. **Description must include:**
    - What changes are included
    - What issue it resolves (if applicable)
    - Testing performed

3. **PR must pass:**
    - ✅ Build & Test (CI pipeline)
    - ✅ Code review (if applicable)
    - ✅ All status checks

### 3️⃣ Merge to Develop

```bash
# Once PR is approved:
# 1. In GitHub: Click "Squash and merge" 
#    (keeps develop history clean)
# 2. Or from CLI:
git checkout develop
git pull origin develop
git merge --squash feat/auth/login-implementation
git commit -m "feat(auth): implement JWT login endpoint (#PR_NUMBER)"
git push origin develop
```

**Important:** Use squash merge for features to keep develop clean.

### 4️⃣ Delete Feature Branch

```bash
# Locally
git branch -d feat/auth/login-implementation

# Remotely (automatic if you used GitHub UI)
git push origin --delete feat/auth/login-implementation
```

---

## Creating a Release

When features are ready for production:

### 1️⃣ Create Release Branch

```bash
git checkout develop
git pull origin develop
git checkout -b release/1.1.0
```

**Versioning:** MAJOR.MINOR.PATCH (Semantic Versioning)

- `1.0.0` → First release
- `1.1.0` → New features
- `1.0.1` → Bugfixes only

### 2️⃣ Prepare Release (Optional)

In release branch you can:

- Update version numbers
- Write release notes
- Apply small bugfixes if needed

```bash
git commit -m "chore(release): bump version to 1.1.0"
git push origin release/1.1.0
```

### 3️⃣ Create PR: release → main

```bash
# In GitHub: Create PR from release/1.1.0 → main
# Description: Release 1.1.0 - [features included]
```

**Requirements for merge to main:**

- ✅ Build & Test passed
- ✅ CodeQL Analysis passed
- ✅ Linear history (required_linear_history)
- ✅ PR required

### 4️⃣ Merge to main and Create Tag

Once merged:

```bash
git checkout main
git pull origin main

# Create version tag
git tag -a v1.1.0 -m "Release 1.1.0"
git push origin v1.1.0

# Also merge release back to develop
git checkout develop
git pull origin develop
git merge main --no-edit
git push origin develop
```

**Or automatically** with GitHub Actions (see `.github/workflows/release.yaml`)

### 5️⃣ Delete Release Branch

```bash
git push origin --delete release/1.1.0
git branch -d release/1.1.0
```

---

## Hotfixes (Production Emergencies)

When there's a critical bug in main:

### 1️⃣ Create Hotfix from main

```bash
git checkout main
git pull origin main
git checkout -b hotfix/1.1.1

# Apply fix
git commit -m "fix(auth): prevent JWT token replay attacks"
git push origin hotfix/1.1.1
```

### 2️⃣ Merge to main

```bash
# PR: hotfix/1.1.1 → main
# Requirements: Same as release (Build & Test + CodeQL)
```

### 3️⃣ Tag and Merge to develop

```bash
git checkout main
git pull origin main
git tag -a v1.1.1 -m "Hotfix 1.1.1"
git push origin v1.1.1

# Also merge to develop so it has the fix
git checkout develop
git pull origin develop
git merge main --no-edit
git push origin develop
```

---

## Commit Conventions

All commits must follow **Conventional Commits**:

```
<type>(<context>): <description>

[optional body]

[optional footer]
```

### Valid Types:

- `feat`: New feature
- `fix`: Bugfix
- `docs`: Documentation
- `style`: Code formatting (no logic change)
- `refactor`: Code refactoring without functionality change
- `perf`: Performance improvements
- `test`: Add or update tests
- `chore`: Internal changes (dependencies, config)
- `ci`: CI/CD changes

### Examples:

```bash
git commit -m "feat(auth): add multi-factor authentication"
git commit -m "fix(user): prevent duplicate email registration"
git commit -m "docs(api): add OpenAPI documentation"
git commit -m "test(order): add payment integration tests"
git commit -m "refactor(cache): simplify Redis decorator pattern"
git commit -m "perf(database): add query indexes for user lookup"
git commit -m "chore(deps): upgrade Spring Boot to 3.1.0"
```

---

## Useful Git Aliases

Add these to your `.gitconfig`:

```bash
git config --global alias.feature '!f() { git checkout -b feat/$1; }; f'
git config --global alias.bugfix '!f() { git checkout -b fix/$1; }; f'
git config --global alias.release '!f() { git checkout -b release/$1; }; f'
git config --global alias.hotfix '!f() { git checkout -b hotfix/$1; }; f'
```

Usage:

```bash
git feature auth/login
# Equivalent to: git checkout -b feat/auth/login
```

---

## Best Practices

### ✅ DO:

- ✅ Commit frequently (every 10-20 lines of important change)
- ✅ Write clear, descriptive commit messages
- ✅ Pull before push to avoid conflicts
- ✅ Use squash merge for features (keeps develop clean)
- ✅ Create version tag on each release
- ✅ Write tests for new features
- ✅ Review changes before committing

### ❌ DON'T:

- ❌ Direct commits to `main` or `develop`
- ❌ Merges without PR (except critical hotfixes)
- ❌ Commits with unrelated changes
- ❌ Push commits without passing Build & Test
- ❌ Change history in shared branches (don't rebase develop)
- ❌ Use `git push --force` except on personal branches
- ❌ Ignore Code Review results

---

## Resolving Conflicts

If conflicts occur during merge:

```bash
# View conflicting files
git status

# Resolve manually or with tools
# Then mark as resolved
git add resolved-file

# Complete merge
git commit -m "Merge branch 'feat/...' (resolved conflicts)"
git push origin develop
```

---

## Complete Example Workflow

**Typical week:**

```
Monday:
  git checkout develop
  git pull origin develop
  git checkout -b feat/auth/reset-password
  
Wednesday:
  git push origin feat/auth/reset-password
  Create PR on GitHub
  Reviewer makes comments
  git commit -m "fix(auth): handle edge case in token validation"
  git push origin feat/auth/reset-password
  
Thursday:
  PR approved ✅
  Merge (squash) to develop on GitHub
  
Friday (Release):
  git checkout develop
  git pull origin develop
  git checkout -b release/2.1.0
  git push origin release/2.1.0
  Create PR: release/2.1.0 → main
  
Friday afternoon:
  PR merged ✅
  git checkout main && git pull
  git tag -a v2.1.0 -m "Release 2.1.0: Password reset flow"
  git push origin v2.1.0
  Merge to develop
  Delete release/2.1.0
  
  Deploy to production ✅
```

---

## FAQ

**Q: Can I commit directly to main?**
A: No. Main is protected by branch rules. Only PRs from release/* and hotfix/* can be merged.

**Q: What if I accidentally committed to main?**
A: Revert the commit: `git revert HEAD` and push. Then create a PR from develop.

**Q: Can I rebase my feature branch?**
A: Yes, on personal branches (feat/*, fix/*). But DO NOT rebase develop.

**Q: Must every commit be functional?**
A: Ideally yes. But in features you can have intermediate commits. They're consolidated with squash.

**Q: When do I use fix/* vs hotfix/?**
A: `fix/*` for normal bugs in features (develop). `hotfix/*` for critical bugs in production (from main).

---

**Last updated:** December 2025
**Project version:** 1.0.0+
