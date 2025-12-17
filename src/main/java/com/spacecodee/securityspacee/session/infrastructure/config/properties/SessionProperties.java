package com.spacecodee.securityspacee.session.infrastructure.config.properties;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Builder;

@Builder
@ConfigurationProperties(prefix = "session")
public record SessionProperties(Duration expiration) {

    public SessionProperties {
        if (expiration == null || expiration.isNegative() || expiration.isZero()) {
            expiration = Duration.ofHours(24);
        }
    }
}
