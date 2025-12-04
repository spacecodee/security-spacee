-- ============================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS RBAC
-- Sistema: Spring Boot + Spring Security + JWT
-- Base de Datos: PostgreSQL 14+
-- Nivel RBAC: 4 (Enterprise)
-- ============================================

-- Extensiones necesarias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- 1. SCHEMA Y TIPOS ENUM
-- ============================================

-- Tipos ENUM para validación a nivel de BD
CREATE TYPE user_type_enum AS ENUM ('HUMAN', 'SYSTEM', 'SERVICE');
CREATE TYPE access_level_enum AS ENUM ('PUBLIC', 'AUTHENTICATED', 'AUTHORIZED');
CREATE TYPE http_method_enum AS ENUM ('GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS', 'HEAD');
CREATE TYPE token_type_enum AS ENUM ('ACCESS', 'REFRESH', 'BEARER');
CREATE TYPE token_state_enum AS ENUM ('ACTIVE', 'EXPIRED', 'REVOKED', 'BLACKLISTED');
CREATE TYPE conflict_type_enum AS ENUM ('STATIC', 'DYNAMIC', 'SESSION');
CREATE TYPE severity_enum AS ENUM ('ERROR', 'WARNING');
CREATE TYPE logout_reason_enum AS ENUM ('manual', 'timeout', 'forced', 'security');

-- ============================================
-- 2. TABLAS PRINCIPALES
-- ============================================

-- Tabla: module (Módulos del sistema)
CREATE TABLE module (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    base_path VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

COMMENT ON TABLE module IS 'System modules for organizing operations';

-- Tabla: operation (Operaciones/endpoints del sistema)
CREATE TABLE operation (
    id SERIAL PRIMARY KEY,
    tag VARCHAR(100) NOT NULL,
    path VARCHAR(500),
    http_method http_method_enum NOT NULL,
    access_level access_level_enum NOT NULL DEFAULT 'AUTHORIZED',
    module_id INTEGER NOT NULL REFERENCES module(id) ON DELETE CASCADE,
    description VARCHAR(300),
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_operation_module_id ON operation(module_id);
CREATE INDEX idx_operation_access_level ON operation(access_level);

COMMENT ON TABLE operation IS 'Operations with simplified access control: PUBLIC (no auth), AUTHENTICATED (auth only), AUTHORIZED (auth + permission)';

-- Tabla: role (Roles del sistema con jerarquía)
CREATE TABLE role (
    role_id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    parent_role_id INTEGER REFERENCES role(role_id) ON DELETE SET NULL,
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    hierarchy_level INTEGER DEFAULT 1 NOT NULL,
    system_role_tag VARCHAR UNIQUE,
    max_users INTEGER CHECK (max_users > 0),
    current_users INTEGER DEFAULT 0 NOT NULL CHECK (current_users >= 0),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_role_parent_role_id ON role(parent_role_id);
CREATE INDEX idx_role_hierarchy_level ON role(hierarchy_level);
CREATE INDEX idx_role_is_active ON role(is_active);

COMMENT ON TABLE role IS 'System roles with hierarchy, inheritance, and cardinality constraints';
COMMENT ON COLUMN role.parent_role_id IS 'Self-reference for role hierarchy';
COMMENT ON COLUMN role.hierarchy_level IS 'Higher level inherits from lower levels';
COMMENT ON COLUMN role.max_users IS 'Cardinality: max users allowed (NULL = unlimited)';
COMMENT ON COLUMN role.current_users IS 'Current count of users with this role';

-- Tabla: user_auth (Autenticación de usuarios)
CREATE TABLE user_auth (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    user_type user_type_enum NOT NULL DEFAULT 'HUMAN',
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    email_verified BOOLEAN DEFAULT FALSE NOT NULL,
    last_login_at TIMESTAMPTZ,
    failed_login_attempts INTEGER DEFAULT 0 NOT NULL CHECK (failed_login_attempts BETWEEN 0 AND 10),
    locked_until TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_user_auth_email ON user_auth(email);
CREATE INDEX idx_user_auth_username ON user_auth(username);
CREATE INDEX idx_user_auth_is_active ON user_auth(is_active);
CREATE INDEX idx_user_auth_user_type ON user_auth(user_type);

COMMENT ON TABLE user_auth IS 'Authentication and security - user_type: HUMAN (has profile), SYSTEM (internal), SERVICE (API/microservice)';
COMMENT ON COLUMN user_auth.password IS 'Hashed password - Min 8 characters (validated in app)';
COMMENT ON COLUMN user_auth.failed_login_attempts IS '0-10 allowed';

-- Tabla: user_profile (Perfil de usuarios HUMAN)
CREATE TABLE user_profile (
    profile_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL UNIQUE REFERENCES user_auth(user_id) ON DELETE CASCADE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    phone_verified BOOLEAN DEFAULT FALSE NOT NULL,
    language_code VARCHAR(10) NOT NULL DEFAULT 'en',
    avatar_url TEXT,
    bio TEXT,
    timezone VARCHAR(50) DEFAULT 'UTC' NOT NULL,
    date_of_birth DATE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_user_profile_user_id ON user_profile(user_id);

COMMENT ON TABLE user_profile IS 'User profile - Only for HUMAN users, NULL for SYSTEM/SERVICE users';

-- ============================================
-- 3. TABLAS DE RELACIÓN USER-ROLE
-- ============================================

-- Tabla: user_role (Relación N:M entre usuarios y roles)
CREATE TABLE user_role (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES user_auth(user_id) ON DELETE CASCADE,
    role_id INTEGER NOT NULL REFERENCES role(role_id) ON DELETE CASCADE,
    assigned_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    assigned_by INTEGER REFERENCES user_auth(user_id) ON DELETE SET NULL,
    valid_from TIMESTAMPTZ,
    valid_until TIMESTAMPTZ,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    assignment_reason VARCHAR(255),
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id),
    CONSTRAINT chk_valid_dates CHECK (valid_from IS NULL OR valid_until IS NULL OR valid_from < valid_until)
);

CREATE INDEX idx_user_role_user_id ON user_role(user_id);
CREATE INDEX idx_user_role_role_id ON user_role(role_id);
CREATE INDEX idx_user_role_is_active ON user_role(is_active);
CREATE INDEX idx_user_role_valid_from ON user_role(valid_from);
CREATE INDEX idx_user_role_valid_until ON user_role(valid_until);

COMMENT ON TABLE user_role IS 'N:M relationship: Users can have multiple roles, validates all constraints before assignment';
COMMENT ON COLUMN user_role.assigned_by IS 'Who assigned this role';
COMMENT ON COLUMN user_role.valid_from IS 'When this role assignment becomes active';
COMMENT ON COLUMN user_role.valid_until IS 'When this role assignment expires';
COMMENT ON COLUMN user_role.is_active IS 'Can be disabled without deleting';

-- ============================================
-- 4. TABLAS DE SESIONES
-- ============================================

-- Tabla: user_session (Sesiones de usuario)
CREATE TABLE user_session (
    session_id VARCHAR(255) PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES user_auth(user_id) ON DELETE CASCADE,
    session_token VARCHAR(500) NOT NULL UNIQUE,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    last_activity_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    logout_at TIMESTAMPTZ,
    logout_reason logout_reason_enum
);

CREATE INDEX idx_user_session_user_id ON user_session(user_id);
CREATE INDEX idx_user_session_session_token ON user_session(session_token);
CREATE INDEX idx_user_session_is_active ON user_session(is_active);
CREATE INDEX idx_user_session_expires_at ON user_session(expires_at);

COMMENT ON TABLE user_session IS 'User sessions with active roles - tracks what roles are active in each session';
COMMENT ON COLUMN user_session.session_token IS 'Unique session identifier';
COMMENT ON COLUMN user_session.logout_reason IS 'manual, timeout, forced, security';

-- Tabla: session_role (Roles activos por sesión)
CREATE TABLE session_role (
    id SERIAL PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL REFERENCES user_session(session_id) ON DELETE CASCADE,
    role_id INTEGER NOT NULL REFERENCES role(role_id) ON DELETE CASCADE,
    activated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deactivated_at TIMESTAMPTZ,
    activation_reason VARCHAR(255),
    requires_justification BOOLEAN DEFAULT FALSE NOT NULL,
    approved_by INTEGER REFERENCES user_auth(user_id) ON DELETE SET NULL,
    approved_at TIMESTAMPTZ,
    auto_deactivate_at TIMESTAMPTZ,
    is_active BOOLEAN DEFAULT TRUE NOT NULL
);

CREATE INDEX idx_session_role ON session_role(session_id, role_id);
CREATE INDEX idx_session_role_session_id ON session_role(session_id);
CREATE INDEX idx_session_role_role_id ON session_role(role_id);
CREATE INDEX idx_session_role_is_active ON session_role(is_active);

COMMENT ON TABLE session_role IS 'Active roles per session - enables dynamic role switching and least privilege principle';
COMMENT ON COLUMN session_role.activation_reason IS 'Why user activated this role';
COMMENT ON COLUMN session_role.approved_by IS 'If approval required';
COMMENT ON COLUMN session_role.auto_deactivate_at IS 'Auto-deactivation time for sensitive roles';

-- ============================================
-- 5. TABLAS DE PERMISOS
-- ============================================

-- Tabla: permission (Permisos granulares CRUD)
CREATE TABLE permission (
    id SERIAL PRIMARY KEY,
    role_id INTEGER NOT NULL REFERENCES role(role_id) ON DELETE CASCADE,
    operation_id INTEGER NOT NULL REFERENCES operation(id) ON DELETE CASCADE,
    can_create BOOLEAN DEFAULT FALSE NOT NULL,
    can_read BOOLEAN DEFAULT FALSE NOT NULL,
    can_update BOOLEAN DEFAULT FALSE NOT NULL,
    can_delete BOOLEAN DEFAULT FALSE NOT NULL,
    valid_from TIMESTAMPTZ,
    valid_until TIMESTAMPTZ,
    granted_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    granted_by INTEGER REFERENCES user_auth(user_id) ON DELETE SET NULL,
    ip_whitelist TEXT,
    conditions JSONB,
    CONSTRAINT uk_permission_role_operation UNIQUE (role_id, operation_id),
    CONSTRAINT chk_permission_valid_dates CHECK (valid_from IS NULL OR valid_until IS NULL OR valid_from < valid_until)
);

CREATE INDEX idx_permission_role_id ON permission(role_id);
CREATE INDEX idx_permission_operation_id ON permission(operation_id);
CREATE INDEX idx_permission_valid_from ON permission(valid_from);
CREATE INDEX idx_permission_valid_until ON permission(valid_until);
CREATE INDEX idx_permission_conditions ON permission USING gin(conditions);

COMMENT ON TABLE permission IS 'Granular CRUD permissions with temporal and context-based constraints - Only checked for AUTHORIZED operations';
COMMENT ON COLUMN permission.valid_from IS 'Temporal: When this permission becomes active';
COMMENT ON COLUMN permission.valid_until IS 'Temporal: When this permission expires';
COMMENT ON COLUMN permission.ip_whitelist IS 'Context: Comma-separated IPs allowed (NULL = all IPs)';
COMMENT ON COLUMN permission.conditions IS 'Context: Additional dynamic conditions (JSON format)';

-- ============================================
-- 6. TABLAS DE RESTRICCIONES DE ROLES
-- ============================================

-- Tabla: role_activation_requirement (Requisitos para activación de roles)
CREATE TABLE role_activation_requirement (
    id SERIAL PRIMARY KEY,
    role_id INTEGER NOT NULL UNIQUE REFERENCES role(role_id) ON DELETE CASCADE,
    requires_mfa BOOLEAN DEFAULT FALSE NOT NULL,
    requires_justification BOOLEAN DEFAULT FALSE NOT NULL,
    requires_approval BOOLEAN DEFAULT FALSE NOT NULL,
    approver_role_id INTEGER REFERENCES role(role_id) ON DELETE SET NULL,
    max_activation_duration INTERVAL,
    max_concurrent_activations INTEGER CHECK (max_concurrent_activations > 0),
    cooldown_period INTERVAL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_role_activation_requirement_role_id ON role_activation_requirement(role_id);

COMMENT ON TABLE role_activation_requirement IS 'Defines requirements and constraints for activating sensitive roles';
COMMENT ON COLUMN role_activation_requirement.requires_mfa IS 'Requires Multi-Factor Authentication to activate';
COMMENT ON COLUMN role_activation_requirement.requires_justification IS 'User must provide reason for activation';
COMMENT ON COLUMN role_activation_requirement.requires_approval IS 'Needs approval from another user';
COMMENT ON COLUMN role_activation_requirement.approver_role_id IS 'Role that can approve activation';
COMMENT ON COLUMN role_activation_requirement.max_activation_duration IS 'Max time role can stay active (e.g., 30 minutes)';
COMMENT ON COLUMN role_activation_requirement.max_concurrent_activations IS 'Max simultaneous active sessions with this role';
COMMENT ON COLUMN role_activation_requirement.cooldown_period IS 'Time before role can be reactivated';

-- Tabla: role_conflict (Separación de Deberes - SoD)
CREATE TABLE role_conflict (
    id SERIAL PRIMARY KEY,
    role_id INTEGER NOT NULL REFERENCES role(role_id) ON DELETE CASCADE,
    conflicting_role_id INTEGER NOT NULL REFERENCES role(role_id) ON DELETE CASCADE,
    conflict_type conflict_type_enum NOT NULL DEFAULT 'STATIC',
    conflict_reason VARCHAR(255),
    severity severity_enum DEFAULT 'ERROR' NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by INTEGER REFERENCES user_auth(user_id) ON DELETE SET NULL,
    CONSTRAINT uk_role_conflict UNIQUE (role_id, conflicting_role_id),
    CONSTRAINT chk_role_conflict_different_roles CHECK (role_id != conflicting_role_id)
);

CREATE INDEX idx_role_conflict_role_id ON role_conflict(role_id);
CREATE INDEX idx_role_conflict_conflicting_role_id ON role_conflict(conflicting_role_id);
CREATE INDEX idx_role_conflict_conflict_type ON role_conflict(conflict_type);

COMMENT ON TABLE role_conflict IS 'SoD: STATIC (never coexist), DYNAMIC (can assign but not activate together), SESSION (not in same session)';
COMMENT ON COLUMN role_conflict.conflict_type IS 'STATIC, DYNAMIC, SESSION';
COMMENT ON COLUMN role_conflict.conflict_reason IS 'Why these roles cannot coexist';
COMMENT ON COLUMN role_conflict.severity IS 'ERROR (prevent), WARNING (alert only)';

-- Tabla: role_prerequisite (Prerequisitos de roles)
CREATE TABLE role_prerequisite (
    id SERIAL PRIMARY KEY,
    role_id INTEGER NOT NULL REFERENCES role(role_id) ON DELETE CASCADE,
    required_role_id INTEGER NOT NULL REFERENCES role(role_id) ON DELETE CASCADE,
    is_mandatory BOOLEAN DEFAULT TRUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uk_role_prerequisite UNIQUE (role_id, required_role_id),
    CONSTRAINT chk_role_prerequisite_different_roles CHECK (role_id != required_role_id)
);

CREATE INDEX idx_role_prerequisite_role_id ON role_prerequisite(role_id);

COMMENT ON TABLE role_prerequisite IS 'Prerequisites: Roles required before assigning another role';
COMMENT ON COLUMN role_prerequisite.role_id IS 'Role that requires prerequisites';
COMMENT ON COLUMN role_prerequisite.required_role_id IS 'Required prerequisite role';
COMMENT ON COLUMN role_prerequisite.is_mandatory IS 'If false, is recommended but not required';

-- Tabla: role_schedule (Restricciones temporales de roles)
CREATE TABLE role_schedule (
    id SERIAL PRIMARY KEY,
    role_id INTEGER NOT NULL REFERENCES role(role_id) ON DELETE CASCADE,
    day_of_week INTEGER CHECK (day_of_week BETWEEN 0 AND 6),
    start_time TIME,
    end_time TIME,
    timezone VARCHAR(50) DEFAULT 'UTC' NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_role_schedule_role_id ON role_schedule(role_id);
CREATE INDEX idx_role_schedule_day_of_week ON role_schedule(day_of_week);

COMMENT ON TABLE role_schedule IS 'Temporal constraints: Time-based role activation (e.g., Night Admin, Weekend Support)';
COMMENT ON COLUMN role_schedule.day_of_week IS '0=Sunday, 1=Monday, ... 6=Saturday (NULL = all days)';
COMMENT ON COLUMN role_schedule.start_time IS 'Time when role becomes active (NULL = 00:00)';
COMMENT ON COLUMN role_schedule.end_time IS 'Time when role becomes inactive (NULL = 23:59)';

-- ============================================
-- 7. TABLA DE JWT TOKENS
-- ============================================

-- Tabla: jwt_token (Gestión de tokens JWT)
CREATE TABLE jwt_token (
    id SERIAL PRIMARY KEY,
    jti VARCHAR(255) NOT NULL UNIQUE,
    token TEXT NOT NULL,
    token_type token_type_enum NOT NULL DEFAULT 'ACCESS',
    user_id INTEGER NOT NULL REFERENCES user_auth(user_id) ON DELETE CASCADE,
    session_id VARCHAR(255) REFERENCES user_session(session_id) ON DELETE SET NULL,
    is_valid BOOLEAN DEFAULT TRUE NOT NULL,
    is_revoked BOOLEAN DEFAULT FALSE NOT NULL,
    state token_state_enum NOT NULL DEFAULT 'ACTIVE',
    issued_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    expiry_date TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    revoked_reason VARCHAR(255),
    revoked_by INTEGER REFERENCES user_auth(user_id) ON DELETE SET NULL,
    refresh_count INTEGER DEFAULT 0 NOT NULL CHECK (refresh_count >= 0),
    last_refresh_at TIMESTAMPTZ,
    previous_token_jti VARCHAR(255),
    usage_count INTEGER DEFAULT 0 NOT NULL CHECK (usage_count >= 0),
    last_access_at TIMESTAMPTZ,
    last_operation VARCHAR(100),
    client_ip VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_jwt_token_jti ON jwt_token(jti);
CREATE INDEX idx_jwt_token_user_id ON jwt_token(user_id);
CREATE INDEX idx_jwt_token_session_id ON jwt_token(session_id);
CREATE INDEX idx_jwt_token_expiry_date ON jwt_token(expiry_date);
CREATE INDEX idx_jwt_token_is_valid ON jwt_token(is_valid);
CREATE INDEX idx_jwt_token_state ON jwt_token(state);
CREATE INDEX idx_jwt_token_token_type ON jwt_token(token_type);

COMMENT ON TABLE jwt_token IS 'JWT token management linked to sessions - invalidate all session tokens at once';
COMMENT ON COLUMN jwt_token.token_type IS 'ACCESS, REFRESH, BEARER';
COMMENT ON COLUMN jwt_token.state IS 'ACTIVE, EXPIRED, REVOKED, BLACKLISTED';
COMMENT ON COLUMN jwt_token.session_id IS 'Link to user session';
COMMENT ON COLUMN jwt_token.client_ip IS 'Supports IPv4 and IPv6';

-- ============================================
-- 8. TRIGGERS PARA UPDATED_AT
-- ============================================

-- Función genérica para actualizar updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicar trigger a todas las tablas con updated_at
CREATE TRIGGER trigger_module_updated_at BEFORE UPDATE ON module
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_operation_updated_at BEFORE UPDATE ON operation
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_role_updated_at BEFORE UPDATE ON role
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_role_activation_requirement_updated_at BEFORE UPDATE ON role_activation_requirement
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_user_auth_updated_at BEFORE UPDATE ON user_auth
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_user_profile_updated_at BEFORE UPDATE ON user_profile
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_jwt_token_updated_at BEFORE UPDATE ON jwt_token
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- 9. FUNCIONES DE VALIDACIÓN Y LÓGICA
-- ============================================

-- Función para validar asignación de roles (cardinalidad)
CREATE OR REPLACE FUNCTION validate_role_cardinality()
RETURNS TRIGGER AS $$
DECLARE
    v_max_users INTEGER;
    v_current_users INTEGER;
BEGIN
    -- Solo validar si se está activando un rol (NEW.is_active = TRUE)
    IF NEW.is_active = TRUE THEN
        SELECT max_users, current_users INTO v_max_users, v_current_users
        FROM role WHERE role_id = NEW.role_id;

        -- Si hay límite y se alcanzó, lanzar error
        IF v_max_users IS NOT NULL AND v_current_users >= v_max_users THEN
            RAISE EXCEPTION 'Role cardinality limit reached. Max users: %', v_max_users;
        END IF;

        -- Incrementar contador de usuarios del rol
        UPDATE role SET current_users = current_users + 1 WHERE role_id = NEW.role_id;
    END IF;

    -- Si se está desactivando (NEW.is_active = FALSE y OLD.is_active = TRUE)
    IF TG_OP = 'UPDATE' AND OLD.is_active = TRUE AND NEW.is_active = FALSE THEN
        UPDATE role SET current_users = GREATEST(current_users - 1, 0) WHERE role_id = NEW.role_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_validate_role_cardinality
    BEFORE INSERT OR UPDATE ON user_role
    FOR EACH ROW EXECUTE FUNCTION validate_role_cardinality();

-- Función para validar conflictos de roles (SoD STATIC)
CREATE OR REPLACE FUNCTION validate_role_conflicts()
RETURNS TRIGGER AS $$
DECLARE
    v_conflict RECORD;
BEGIN
    -- Solo validar si el rol está activo
    IF NEW.is_active = TRUE THEN
        -- Verificar conflictos STATIC
        FOR v_conflict IN
            SELECT rc.conflicting_role_id, rc.conflict_reason, rc.severity
            FROM role_conflict rc
            WHERE rc.role_id = NEW.role_id AND rc.conflict_type = 'STATIC'
            AND rc.severity = 'ERROR'
        LOOP
            -- Verificar si el usuario ya tiene el rol conflictivo
            IF EXISTS (
                SELECT 1 FROM user_role ur
                WHERE ur.user_id = NEW.user_id
                AND ur.role_id = v_conflict.conflicting_role_id
                AND ur.is_active = TRUE
            ) THEN
                RAISE EXCEPTION 'Role conflict detected: %. User cannot have both roles.', v_conflict.conflict_reason;
            END IF;
        END LOOP;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_validate_role_conflicts
    BEFORE INSERT OR UPDATE ON user_role
    FOR EACH ROW EXECUTE FUNCTION validate_role_conflicts();

-- Función para actualizar last_activity_at en sesiones
CREATE OR REPLACE FUNCTION update_session_activity()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE user_session
    SET last_activity_at = CURRENT_TIMESTAMP
    WHERE session_id = NEW.session_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_session_activity
    AFTER INSERT OR UPDATE ON session_role
    FOR EACH ROW EXECUTE FUNCTION update_session_activity();

-- ============================================
-- 10. VISTAS ÚTILES
-- ============================================

-- Vista: Usuarios con sus roles activos
CREATE OR REPLACE VIEW v_user_roles AS
SELECT
    ua.user_id,
    ua.username,
    ua.email,
    ua.user_type,
    r.role_id,
    r.role_name,
    r.hierarchy_level,
    ur.assigned_at,
    ur.assigned_by,
    ur.valid_from,
    ur.valid_until,
    ur.is_active,
    CASE
        WHEN ur.valid_from IS NOT NULL AND ur.valid_from > CURRENT_TIMESTAMP THEN 'PENDING'
        WHEN ur.valid_until IS NOT NULL AND ur.valid_until < CURRENT_TIMESTAMP THEN 'EXPIRED'
        WHEN ur.is_active = TRUE THEN 'ACTIVE'
        ELSE 'INACTIVE'
    END AS role_status
FROM user_auth ua
JOIN user_role ur ON ua.user_id = ur.user_id
JOIN role r ON ur.role_id = r.role_id;

-- Vista: Permisos efectivos por usuario
CREATE OR REPLACE VIEW v_user_permissions AS
SELECT DISTINCT
    ur.user_id,
    p.operation_id,
    o.tag,
    o.path,
    o.http_method,
    o.access_level,
    m.name AS module_name,
    p.can_create,
    p.can_read,
    p.can_update,
    p.can_delete,
    r.role_name,
    r.hierarchy_level
FROM user_role ur
JOIN role r ON ur.role_id = r.role_id
JOIN permission p ON r.role_id = p.role_id
JOIN operation o ON p.operation_id = o.id
JOIN module m ON o.module_id = m.id
WHERE ur.is_active = TRUE
AND r.is_active = TRUE
AND o.is_active = TRUE
AND (ur.valid_from IS NULL OR ur.valid_from <= CURRENT_TIMESTAMP)
AND (ur.valid_until IS NULL OR ur.valid_until > CURRENT_TIMESTAMP)
AND (p.valid_from IS NULL OR p.valid_from <= CURRENT_TIMESTAMP)
AND (p.valid_until IS NULL OR p.valid_until > CURRENT_TIMESTAMP);

-- Vista: Sesiones activas con roles
CREATE OR REPLACE VIEW v_active_sessions AS
SELECT
    us.session_id,
    us.user_id,
    ua.username,
    us.created_at,
    us.expires_at,
    us.last_activity_at,
    us.ip_address,
    COUNT(sr.id) FILTER (WHERE sr.is_active = TRUE) AS active_roles_count,
    ARRAY_AGG(r.role_name) FILTER (WHERE sr.is_active = TRUE) AS active_roles
FROM user_session us
JOIN user_auth ua ON us.user_id = ua.user_id
LEFT JOIN session_role sr ON us.session_id = sr.session_id
LEFT JOIN role r ON sr.role_id = r.role_id
WHERE us.is_active = TRUE
AND us.expires_at > CURRENT_TIMESTAMP
GROUP BY us.session_id, us.user_id, ua.username, us.created_at, us.expires_at, us.last_activity_at, us.ip_address;

-- ============================================
-- FIN DEL SCRIPT
-- ============================================
