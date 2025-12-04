# RBAC Authentication & Authorization Database Schema (PostgreSQL)

This document describes the relational schema for an enterprise-grade RBAC (Role-Based Access Control) system designed
for Spring Boot, Spring Security, JWT, and PostgreSQL. It covers tables, columns, constraints, indexes, relationships,
triggers, functions, and views.

---

## Overview

The schema implements:

- User authentication and profiles
- Roles with hierarchy and cardinality
- Operations (endpoints) grouped by modules
- Role-based permissions with CRUD granularity
- Session-based role activation (dynamic RBAC)
- Separation of duties (SoD) via role conflicts and prerequisites
- Time-based role activation windows
- JWT token management linked to sessions

---

## Enumerated Types

The schema uses PostgreSQL enums to enforce valid values at database level:

- `user_type_enum`: `HUMAN`, `SYSTEM`, `SERVICE`
- `access_level_enum`: `PUBLIC`, `AUTHENTICATED`, `AUTHORIZED`
- `http_method_enum`: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`, `HEAD`
- `token_type_enum`: `ACCESS`, `REFRESH`, `BEARER`
- `token_state_enum`: `ACTIVE`, `EXPIRED`, `REVOKED`, `BLACKLISTED`
- `conflict_type_enum`: `STATIC`, `DYNAMIC`, `SESSION`
- `severity_enum`: `ERROR`, `WARNING`
- `logout_reason_enum`: `manual`, `timeout`, `forced`, `security`

These types improve integrity and make access-control logic more explicit at the DB layer.

---

## Table: `module`

Represents logical modules of the system (e.g., `USER_MANAGEMENT`, `BILLING`).

### Key Columns

- `id` (PK, serial): Unique module identifier.
- `name` (varchar(100), unique, not null): Human-readable module name.
- `base_path` (varchar(255), unique, not null): Base URL path for the module.
- `description` (varchar(500), nullable): Functional description.
- `is_active` (boolean, default true): Allows deactivating modules logically.
- `created_at`, `updated_at` (timestamptz): Audit timestamps.

### Indexes

- Unique on `name`
- Unique on `base_path`

### Usage

Modules group operations (endpoints) and help organize permissions and documentation.

---

## Table: `operation`

Represents an operation or endpoint in the system, usually an HTTP API.

### Key Columns

- `id` (PK, serial)
- `tag` (varchar(100), not null): Logical name/tag for the operation.
- `path` (varchar(500), nullable): URL path or pattern (e.g., `/users/{id}`).
- `http_method` (`http_method_enum`, not null): HTTP verb.
- `access_level` (`access_level_enum`, default `AUTHORIZED`): High-level access rule.
    - `PUBLIC`: No authentication
    - `AUTHENTICATED`: Logged-in users, no specific permission
    - `AUTHORIZED`: Requires explicit permission
- `module_id` (int, FK → `module.id`, not null): Operation’s parent module.
- `description` (varchar(300), nullable)
- `is_active` (boolean, default true)
- `created_at`, `updated_at` (timestamptz)

### Indexes

- `module_id`
- `access_level`

### Relationships

- Many operations belong to one module.
- Permissions reference operations to define which roles can access them.

### Usage

Defines the universe of controllable actions in the system. Security logic maps users’ roles to these operations via
`permission`.

---

## Table: `role`

Represents roles with hierarchy and cardinality constraints.

### Key Columns

- `role_id` (PK, serial)
- `role_name` (varchar(50), unique, not null): Business name (e.g., `ADMIN`, `SUPPORT_AGENT`).
- `parent_role_id` (int, FK → `role.role_id`, nullable): Hierarchical parent role.
- `description` (varchar(255), nullable)
- `is_active` (boolean, default true)
- `hierarchy_level` (int, default 1): Level used for inheritance/ordering.
- `system_role_tag` (varchar, unique, nullable): Stable system identifier.
- `max_users` (int, nullable): Maximum number of users allowed for this role (`NULL` = unlimited).
- `current_users` (int, default 0): Current number of users assigned to the role.
- `created_at`, `updated_at` (timestamptz)

### Indexes

- `parent_role_id`
- `hierarchy_level`
- `is_active`

### Relationships

- Self-referential hierarchy via `parent_role_id`.
- Related to:
    - `user_role` (assignment to users)
    - `permission` (granted operations)
    - `role_activation_requirement`, `role_conflict`, `role_prerequisite`, `role_schedule`
    - `session_role` (activation per session)

### Usage

Defines access-control roles, supports hierarchical inheritance of semantics, and enforces cardinality limits.

---

## Table: `user_auth`

Core authentication account table.

### Key Columns

- `user_id` (PK, serial)
- `username` (varchar(50), unique, not null)
- `password` (varchar(255), not null): Hashed password (validated at application layer).
- `email` (varchar(255), unique, not null)
- `user_type` (`user_type_enum`, default `HUMAN`):
    - `HUMAN`: End users with profiles.
    - `SYSTEM`: Internal system users (e.g., background jobs).
    - `SERVICE`: API/service accounts.
- `is_active` (boolean, default true)
- `email_verified` (boolean, default false)
- `last_login_at` (timestamptz, nullable)
- `failed_login_attempts` (int, default 0, check 0–10)
- `locked_until` (timestamptz, nullable)
- `created_at`, `updated_at` (timestamptz)

### Indexes

- `email`
- `username`
- `is_active`
- `user_type`

### Relationships

- `user_profile` (1:1)
- `user_role` (1:N)
- `user_session` (1:N)
- `jwt_token` (1:N)
- As FK `assigned_by`, `created_by`, `approved_by`, `revoked_by` in several tables.

### Usage

Represents identities that can authenticate, with security metadata for account locking and audit.

---

## Table: `user_profile`

Extended profile data for human users.

### Key Columns

- `profile_id` (PK, serial)
- `user_id` (int, unique, FK → `user_auth.user_id`, not null)
- `first_name` (varchar(100), not null)
- `last_name` (varchar(100), not null)
- `phone_number` (varchar(20), nullable)
- `phone_verified` (boolean, default false)
- `language_code` (varchar(10), default `en`)
- `avatar_url` (text, nullable)
- `bio` (text, nullable)
- `timezone` (varchar(50), default `UTC`)
- `date_of_birth` (date, nullable)
- `created_at`, `updated_at` (timestamptz)

### Indexes

- `user_id` (unique via FK)

### Relationships

- Strict 1:1 with `user_auth` for `HUMAN` type users.

### Usage

Stores user-facing profile attributes separate from authentication credentials.

---

## Table: `user_role`

Assignment of roles to users, including validity windows.

### Key Columns

- `id` (PK, serial)
- `user_id` (int, FK → `user_auth.user_id`, not null)
- `role_id` (int, FK → `role.role_id`, not null)
- `assigned_at` (timestamptz, default now)
- `assigned_by` (int, FK → `user_auth.user_id`, nullable): Who granted this role.
- `valid_from` (timestamptz, nullable)
- `valid_until` (timestamptz, nullable)
- `is_active` (boolean, default true)
- `assignment_reason` (varchar(255), nullable)

### Constraints

- Unique `(user_id, role_id)` to avoid duplicates.
- Check `valid_from < valid_until` when both are non-null.

### Indexes

- `user_id`
- `role_id`
- `is_active`
- `valid_from`
- `valid_until`

### Relationships

- Many-to-many between `user_auth` and `role`.
- Checked by triggers for:
    - Role cardinality (`role.current_users` vs `role.max_users`)
    - Static role conflicts (`role_conflict`)

### Usage

Central point for user-role assignment. Drives permission resolution and SoD enforcement.

---

## Table: `user_session`

Logged-in sessions for users.

### Key Columns

- `session_id` (varchar(255), PK): Session identifier.
- `user_id` (int, FK → `user_auth.user_id`, not null)
- `session_token` (varchar(500), unique, not null): Unique token for session identification.
- `ip_address` (varchar(45), nullable): IPv4/IPv6 address.
- `user_agent` (text, nullable)
- `created_at` (timestamptz, default now)
- `expires_at` (timestamptz, not null)
- `last_activity_at` (timestamptz, default now)
- `is_active` (boolean, default true)
- `logout_at` (timestamptz, nullable)
- `logout_reason` (`logout_reason_enum`, nullable)

### Indexes

- `user_id`
- `session_token`
- `is_active`
- `expires_at`

### Relationships

- `session_role` (1:N): Tracks which roles are active in each session.
- `jwt_token` (1:N): Tokens are associated with a session when applicable.

### Usage

Provides session-level context for dynamic RBAC (e.g., role activation per session).

---

## Table: `session_role`

Roles activated within a particular session.

### Key Columns

- `id` (PK, serial)
- `session_id` (varchar(255), FK → `user_session.session_id`, not null)
- `role_id` (int, FK → `role.role_id`, not null)
- `activated_at` (timestamptz, default now)
- `deactivated_at` (timestamptz, nullable)
- `activation_reason` (varchar(255), nullable)
- `requires_justification` (boolean, default false)
- `approved_by` (int, FK → `user_auth.user_id`, nullable)
- `approved_at` (timestamptz, nullable)
- `auto_deactivate_at` (timestamptz, nullable)
- `is_active` (boolean, default true)

### Indexes

- Composite `(session_id, role_id)`
- `session_id`
- `role_id`
- `is_active`

### Relationships

- Links active roles back to sessions and roles.
- Triggers update session activity timestamps via `user_session.last_activity_at`.

### Usage

Implements dynamic role activation; a user can have multiple assigned roles but only activate a subset per session for
least-privilege control.

---

## Table: `permission`

Granular CRUD permission for a role on a specific operation.

### Key Columns

- `id` (PK, serial)
- `role_id` (int, FK → `role.role_id`, not null)
- `operation_id` (int, FK → `operation.id`, not null)
- `can_create`, `can_read`, `can_update`, `can_delete` (boolean, default false)
- `valid_from`, `valid_until` (timestamptz, nullable)
- `granted_at` (timestamptz, default now)
- `granted_by` (int, FK → `user_auth.user_id`, nullable)
- `ip_whitelist` (text, nullable): Comma-separated allowed IPs or ranges.
- `conditions` (jsonb, nullable): Additional dynamic condition metadata.

### Constraints

- Unique `(role_id, operation_id)`.
- Check `valid_from < valid_until` when both non-null.

### Indexes

- `role_id`
- `operation_id`
- `valid_from`
- `valid_until`
- GIN index on `conditions` for JSON querying.

### Relationships

- Binds roles to operations.
- Combined with user-role assignments and sessions to compute effective access.

### Usage

Core driver for authorization decisions when `operation.access_level = AUTHORIZED`.

---

## Table: `role_activation_requirement`

Constraints for activating sensitive roles.

### Key Columns

- `id` (PK, serial)
- `role_id` (int, unique, FK → `role.role_id`, not null)
- `requires_mfa` (boolean, default false)
- `requires_justification` (boolean, default false)
- `requires_approval` (boolean, default false)
- `approver_role_id` (int, FK → `role.role_id`, nullable)
- `max_activation_duration` (interval, nullable)
- `max_concurrent_activations` (int, nullable, > 0)
- `cooldown_period` (interval, nullable)
- `created_at`, `updated_at` (timestamptz)

### Indexes

- `role_id`

### Relationships

- 1:1 with `role` (per role requirement set).
- `approver_role_id` references the role(s) allowed to approve activation.

### Usage

Used at application level to enforce advanced activation policies (MFA, approvals, time limits, cooldowns).

---

## Table: `role_conflict`

Defines separation-of-duties conflicts between roles.

### Key Columns

- `id` (PK, serial)
- `role_id` (int, FK → `role.role_id`, not null)
- `conflicting_role_id` (int, FK → `role.role_id`, not null)
- `conflict_type` (`conflict_type_enum`, default `STATIC`):
    - `STATIC`: Cannot be assigned together.
    - `DYNAMIC`: Assignable but not simultaneously active.
    - `SESSION`: Cannot be active in the same session.
- `conflict_reason` (varchar(255), nullable)
- `severity` (`severity_enum`, default `ERROR`):
    - `ERROR`: Strictly prevented.
    - `WARNING`: Logged/alerted but allowed.
- `created_at` (timestamptz, default now)
- `created_by` (int, FK → `user_auth.user_id`, nullable)

### Constraints

- Unique `(role_id, conflicting_role_id)`.
- Check `role_id != conflicting_role_id`.

### Indexes

- `role_id`
- `conflicting_role_id`
- `conflict_type`

### Relationships

- Used by triggers on `user_role` to prevent static conflicts at assignment time (for `severity = ERROR`).

### Usage

Implements SoD rules to avoid dangerous combinations of responsibilities.

---

## Table: `role_prerequisite`

Required or recommended roles before assigning another role.

### Key Columns

- `id` (PK, serial)
- `role_id` (int, FK → `role.role_id`, not null): Role that requires prerequisites.
- `required_role_id` (int, FK → `role.role_id`, not null): Required/recommended role.
- `is_mandatory` (boolean, default true)
- `description` (varchar(255), nullable)
- `created_at` (timestamptz, default now)

### Constraints

- Unique `(role_id, required_role_id)`.
- Check `role_id != required_role_id`.

### Indexes

- `role_id`

### Usage

The application layer can enforce that users must hold certain roles before receiving more privileged ones.

---

## Table: `role_schedule`

Time-based constraints for role usage.

### Key Columns

- `id` (PK, serial)
- `role_id` (int, FK → `role.role_id`, not null)
- `day_of_week` (int, nullable, 0–6): 0 = Sunday, 6 = Saturday. `NULL` = all days.
- `start_time` (time, nullable): Start of allowed window. `NULL` can mean 00:00.
- `end_time` (time, nullable): End of allowed window. `NULL` can mean 23:59.
- `timezone` (varchar(50), default `UTC`)
- `is_active` (boolean, default true)
- `created_at` (timestamptz, default now)

### Indexes

- `role_id`
- `day_of_week`

### Usage

Application enforces whether a role may be active given current day/time and timezone (e.g., “Night Admin”, “Weekend
Support”).

---

## Table: `jwt_token`

Manages JWTs issued to users, linked to sessions.

### Key Columns

- `id` (PK, serial)
- `jti` (varchar(255), unique, not null): JWT ID.
- `token` (text, not null): The raw token (optional to store; depends on security requirements).
- `token_type` (`token_type_enum`, default `ACCESS`)
- `user_id` (int, FK → `user_auth.user_id`, not null)
- `session_id` (varchar(255), FK → `user_session.session_id`, nullable)
- `is_valid` (boolean, default true)
- `is_revoked` (boolean, default false)
- `state` (`token_state_enum`, default `ACTIVE`)
- `issued_at` (timestamptz, default now)
- `expiry_date` (timestamptz, not null)
- `revoked_at` (timestamptz, nullable)
- `revoked_reason` (varchar(255), nullable)
- `revoked_by` (int, FK → `user_auth.user_id`, nullable)
- `refresh_count` (int, default 0, ≥ 0)
- `last_refresh_at` (timestamptz, nullable)
- `previous_token_jti` (varchar(255), nullable)
- `usage_count` (int, default 0, ≥ 0)
- `last_access_at` (timestamptz, nullable)
- `last_operation` (varchar(100), nullable)
- `client_ip` (varchar(45), nullable)
- `user_agent` (text, nullable)
- `created_at`, `updated_at` (timestamptz)

### Indexes

- `jti`
- `user_id`
- `session_id`
- `expiry_date`
- `is_valid`
- `state`
- `token_type`

### Usage

Allows server-side token revocation, audit, and session-wide invalidation by linking tokens to sessions and users.

---

## Triggers and Functions

### Function: `update_updated_at_column()`

Generic trigger function that sets `NEW.updated_at = CURRENT_TIMESTAMP` on updates.

#### Usage

Attached as `BEFORE UPDATE` trigger to:

- `module`
- `operation`
- `role`
- `role_activation_requirement`
- `user_auth`
- `user_profile`
- `jwt_token`

Ensures audit field `updated_at` is always current.

---

### Function: `validate_role_cardinality()`

Enforces role cardinality and maintains `role.current_users`.

#### Behavior

- On `INSERT` or `UPDATE` of `user_role`:
    - If `NEW.is_active = TRUE`:
        - Loads `max_users` and `current_users` for `NEW.role_id`.
        - If `max_users` is not null and `current_users >= max_users`, raises an exception.
        - Otherwise increments `role.current_users`.
    - On update from active to inactive (`OLD.is_active = TRUE`, `NEW.is_active = FALSE`):
        - Decrements `role.current_users` (never below 0).

#### Trigger

- `BEFORE INSERT OR UPDATE` on `user_role`.

---

### Function: `validate_role_conflicts()`

Prevents assignment of statically conflicting roles.

#### Behavior

- On `INSERT` or `UPDATE` of `user_role`:
    - If `NEW.is_active = TRUE`:
        - For each `role_conflict` entry with:
            - `role_id = NEW.role_id`
            - `conflict_type = 'STATIC'`
            - `severity = 'ERROR'`
        - Checks if the user already has the `conflicting_role_id` active.
        - If yes, raises exception with conflict reason.

#### Trigger

- `BEFORE INSERT OR UPDATE` on `user_role`.

---

### Function: `update_session_activity()`

Keeps session activity timestamp up-to-date based on role changes.

#### Behavior

- On `INSERT` or `UPDATE` of `session_role`:
    - Updates `user_session.last_activity_at` for `NEW.session_id` to `CURRENT_TIMESTAMP`.

#### Trigger

- `AFTER INSERT OR UPDATE` on `session_role`.

---

## Views

### View: `v_user_roles`

Shows users with their assigned roles and a computed role status.

#### Columns (main)

- User info: `user_id`, `username`, `email`, `user_type`
- Role info: `role_id`, `role_name`, `hierarchy_level`
- Assignment info: `assigned_at`, `assigned_by`, `valid_from`, `valid_until`, `is_active`
- `role_status` (derived):
    - `PENDING`: `valid_from` in the future
    - `EXPIRED`: `valid_until` in the past
    - `ACTIVE`: currently active
    - `INACTIVE`: otherwise

#### Sources

- `user_auth` + `user_role` + `role`

#### Usage

Handy for admin UIs and reporting, to see who has which roles and their current status.

---

### View: `v_user_permissions`

Effective permissions per user, derived from roles and permissions.

#### Columns (main)

- `user_id`
- Operation info: `operation_id`, `tag`, `path`, `http_method`, `access_level`
- Module info: `module_name`
- Permission flags: `can_create`, `can_read`, `can_update`, `can_delete`
- Role info: `role_name`, `hierarchy_level`

#### Filters

- `user_role.is_active = TRUE`
- `role.is_active = TRUE`
- `operation.is_active = TRUE`
- Validity windows (`valid_from`/`valid_until`) for user-role and permission.

#### Sources

- `user_role` + `role` + `permission` + `operation` + `module`

#### Usage

Used to quickly resolve what operations a user is allowed to execute at a given time.

---

### View: `v_active_sessions`

Aggregates active sessions with the roles active in each session.

#### Columns (main)

- Session info: `session_id`, `user_id`, `created_at`, `expires_at`, `last_activity_at`, `ip_address`
- User info: `username`
- `active_roles_count`: count of active `session_role` entries.
- `active_roles`: array of active role names.

#### Filters

- `user_session.is_active = TRUE`
- `user_session.expires_at > CURRENT_TIMESTAMP`

#### Sources

- `user_session` + `user_auth` + `session_role` + `role`

#### Usage

Useful for monitoring logged-in users, auditing active roles per session, and building admin dashboards.

---

## High-Level Relationships Summary

- `user_auth` 1–1 `user_profile`
- `user_auth` 1–N `user_role` N–1 `role`
- `role` 1–N `permission` N–1 `operation` N–1 `module`
- `user_auth` 1–N `user_session` 1–N `session_role` N–1 `role`
- `role` 1–1 `role_activation_requirement`
- `role` N–N `role` via `role_conflict` and `role_prerequisite`
- `role` 1–N `role_schedule`
- `user_auth` 1–N `jwt_token` N–1 `user_session`

Together, these entities implement a flexible, auditable, and highly configurable RBAC platform suitable for enterprise
applications.

