# 🐙 Branch Protection Rules - Security Spacee

Este documento describe las reglas de protección de ramas que deben configurarse en GitHub.

## 📋 Configuración en GitHub

Ir a: **Settings → Branches → Add branch protection rule**

---

## 🔒 Rama `main` (Producción)

| Configuración                                              | Valor                                     |
|------------------------------------------------------------|-------------------------------------------|
| **Branch name pattern**                                    | `main`                                    |
| **Require a pull request before merging**                  | ✅                                         |
| **Required approvals**                                     | 1 (mínimo)                                |
| **Dismiss stale PR approvals when new commits are pushed** | ✅                                         |
| **Require review from code owners**                        | ✅ (opcional)                              |
| **Require status checks to pass before merging**           | ✅                                         |
| **Status checks required**                                 | `🏗️ Build & Test`, `🛡️ CodeQL Analysis` |
| **Require branches to be up to date**                      | ✅                                         |
| **Require conversation resolution**                        | ✅                                         |
| **Require signed commits**                                 | ❌ (opcional)                              |
| **Require linear history**                                 | ✅                                         |
| **Do not allow bypassing**                                 | ✅                                         |
| **Restrict who can push**                                  | Solo `release/*` y `hotfix/*` vía PR      |
| **Allow force pushes**                                     | ❌                                         |
| **Allow deletions**                                        | ❌                                         |

---

## 🔒 Rama `develop` (Staging/Integración)

| Configuración                             | Valor              |
|-------------------------------------------|--------------------|
| **Branch name pattern**                   | `develop`          |
| **Require a pull request before merging** | ✅                  |
| **Required approvals**                    | 1                  |
| **Dismiss stale PR approvals**            | ✅                  |
| **Require status checks to pass**         | ✅                  |
| **Status checks required**                | `🏗️ Build & Test` |
| **Require branches to be up to date**     | ✅                  |
| **Require conversation resolution**       | ✅                  |
| **Allow force pushes**                    | ❌                  |
| **Allow deletions**                       | ❌                  |

---

## 📝 CODEOWNERS (Opcional)

Crear archivo `.github/CODEOWNERS`:

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

## 🏷️ Environments (GitHub Environments)

### Staging

- **URL**: https://staging.security-spacee.example.com
- **Protection rules**:
    - Wait timer: 0 minutes
    - Required reviewers: Ninguno (auto-deploy en develop)

### Production

- **URL**: https://security-spacee.example.com
- **Protection rules**:
    - Wait timer: 5 minutes
    - Required reviewers: 1 persona
    - Branch: `main` only

---

## 🔑 Secrets Requeridos

Configurar en **Settings → Secrets and variables → Actions**:

### Repository Secrets

| Secret                        | Descripción | Requerido para |
|-------------------------------|-------------|----------------|
| `GITHUB_TOKEN`                | Automático  | Todos          |
| (Ninguno adicional por ahora) | -           | -              |

### Environment Secrets (Production)

| Secret           | Descripción              |
|------------------|--------------------------|
| `DB_PASSWORD`    | Contraseña de PostgreSQL |
| `JWT_SECRET`     | Clave secreta JWT        |
| `REDIS_PASSWORD` | Contraseña de Redis      |

---

## 🚀 Flujo de Trabajo Completo

```
┌─────────────────────────────────────────────────────────────────────┐
│                           DESARROLLO                                 │
├─────────────────────────────────────────────────────────────────────┤
│  1. git checkout develop                                            │
│  2. git checkout -b feat/auth/login-logic                          │
│  3. ... hacer cambios ...                                           │
│  4. git commit -m "feat(auth): add login validation"               │
│  5. git push origin feat/auth/login-logic                          │
│  6. Crear PR → develop                                              │
│  7. CI Pipeline corre automáticamente                               │
│  8. Review + Approve                                                │
│  9. Merge a develop                                                 │
└─────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                            STAGING                                   │
├─────────────────────────────────────────────────────────────────────┤
│  • develop branch se despliega automáticamente a staging            │
│  • Testing manual / QA                                              │
│  • Security scans semanales                                         │
└─────────────────────────────────────────────────────────────────────┘
                                 │
                                 ▼
┌─────────────────────────────────────────────────────────────────────┐
│                           RELEASE                                    │
├─────────────────────────────────────────────────────────────────────┤
│  1. git checkout develop                                            │
│  2. git checkout -b release/1.0.0                                  │
│  3. Bump version, changelog updates                                 │
│  4. Crear PR → main                                                 │
│  5. Review + Approve                                                │
│  6. Merge a main                                                    │
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
│  • Health checks post-deploy                                        │
└─────────────────────────────────────────────────────────────────────┘
```

---

## ⚡ Comandos Útiles

```bash
# Crear feature branch
git checkout develop
git pull origin develop
git checkout -b feat/user/registration

# Crear release
git checkout develop
git pull origin develop
git checkout -b release/1.0.0
# ... bump versions ...
git push origin release/1.0.0
# Crear PR a main

# Crear tag después de merge a main
git checkout main
git pull origin main
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0

# Hotfix (bug crítico en producción)
git checkout main
git pull origin main
git checkout -b hotfix/1.0.1
# ... fix bug ...
git push origin hotfix/1.0.1
# Crear PR a main Y a develop
```
