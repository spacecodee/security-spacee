package com.spacecodee.securityspacee.auth.application.port.out;

import java.time.Instant;

public record CreateSessionCommand(
        Integer userId,
        String ipAddress,
        String userAgent,
        Instant loginTimestamp) {
}
