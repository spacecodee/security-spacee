package com.spacecodee.securityspacee.jwttoken.domain.event;

import java.time.Instant;

import lombok.Builder;

@Builder
public record TokenValidatedInStrictModeEvent(
        String jti,
        Integer userId,
        String sessionId,
        Instant validatedAt,
        Integer usageCount,
        String ipAddress,
        String endpoint) {
}
