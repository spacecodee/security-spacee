package com.spacecodee.securityspacee.session.domain.valueobject;

import org.jspecify.annotations.NonNull;

public enum LogoutReason {
    MANUAL,
    TIMEOUT,
    FORCED,
    ADMIN_ACTION,
    SECURITY,
    PASSWORD_CHANGED,
    ACCOUNT_DELETED,
    SESSION_EXPIRED;

    public static @NonNull LogoutReason fromString(@NonNull String reason) {
        return switch (reason.toLowerCase()) {
            case "manual", "manual_logout" -> MANUAL;
            case "timeout", "idle_timeout" -> TIMEOUT;
            case "forced", "session_limit_reached" -> FORCED;
            case "admin", "admin_action" -> ADMIN_ACTION;
            case "security", "security_breach" -> SECURITY;
            case "password_changed" -> PASSWORD_CHANGED;
            case "account_deleted" -> ACCOUNT_DELETED;
            default -> SESSION_EXPIRED;
        };
    }
}
