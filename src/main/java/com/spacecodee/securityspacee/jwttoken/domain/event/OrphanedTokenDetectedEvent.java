package com.spacecodee.securityspacee.jwttoken.domain.event;

import java.time.Instant;

import lombok.Builder;

@Builder
public record OrphanedTokenDetectedEvent(
        String jti,
        Integer userId,
        String sessionId,
        Instant detectedAt) {
}
