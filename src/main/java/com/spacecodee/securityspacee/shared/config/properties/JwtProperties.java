package com.spacecodee.securityspacee.shared.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.jwt")
public record JwtProperties(
        String secret,
        long accessExpiration,
        long refreshExpiration,
        String issuer,
        String audience) {
}
