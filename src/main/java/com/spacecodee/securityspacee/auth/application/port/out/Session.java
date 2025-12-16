package com.spacecodee.securityspacee.auth.application.port.out;

import java.time.Instant;

public record Session(
        String sessionId,
        Integer userId,
        Instant expiresAt) {
}
