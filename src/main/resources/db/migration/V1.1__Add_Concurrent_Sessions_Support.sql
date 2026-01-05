-- FR-012: Concurrent Sessions - Add device and location tracking columns
-- Migration Version: V1.1
-- Description: Adds device fingerprinting, device name, geolocation, and trusted device flag
-- to support multi-device session management

-- Add device fingerprint column (SHA-256 hash of IP + User Agent)
ALTER TABLE IF EXISTS user_session
    ADD COLUMN IF NOT EXISTS device_fingerprint VARCHAR(32);

-- Add device name column (friendly name like "iPhone 13 Pro - Safari 16")
ALTER TABLE IF EXISTS user_session
    ADD COLUMN IF NOT EXISTS device_name VARCHAR(255);

-- Add location columns (GeoIP data)
ALTER TABLE IF EXISTS user_session
    ADD COLUMN IF NOT EXISTS location_city VARCHAR(100);

ALTER TABLE IF EXISTS user_session
    ADD COLUMN IF NOT EXISTS location_country VARCHAR(100);

ALTER TABLE IF EXISTS user_session
    ADD COLUMN IF NOT EXISTS location_country_code VARCHAR(3);

ALTER TABLE IF EXISTS user_session
    ADD COLUMN IF NOT EXISTS location_latitude DOUBLE PRECISION;

ALTER TABLE IF EXISTS user_session
    ADD COLUMN IF NOT EXISTS location_longitude DOUBLE PRECISION;

-- Add trusted device flag (indicates if device was previously used)
ALTER TABLE IF EXISTS user_session
    ADD COLUMN IF NOT EXISTS is_trusted_device BOOLEAN NOT NULL DEFAULT FALSE;

-- Create index for efficient querying of sessions by device fingerprint
CREATE INDEX IF NOT EXISTS idx_user_session_device_fingerprint ON user_session (device_fingerprint);

-- Create composite index for user_id + device_fingerprint lookups
CREATE INDEX IF NOT EXISTS idx_user_session_user_device ON user_session (user_id, device_fingerprint);

-- Create index for active sessions (most common query)
CREATE INDEX IF NOT EXISTS idx_user_session_active ON user_session (user_id, is_active) WHERE is_active = TRUE;
