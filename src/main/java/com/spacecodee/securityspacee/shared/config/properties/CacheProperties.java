package com.spacecodee.securityspacee.shared.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application.cache.ttl")
public record CacheProperties(
        long userProfile,
        long userRoles,
        long permissions,
        long jwtBlacklist) {
}
