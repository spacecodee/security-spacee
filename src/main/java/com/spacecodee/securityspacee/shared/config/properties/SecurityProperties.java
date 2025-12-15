package com.spacecodee.securityspacee.shared.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.security")
public record SecurityProperties(
        String allowedOrigins,
        int maxLoginAttempts,
        long accountLockDuration) {
}
