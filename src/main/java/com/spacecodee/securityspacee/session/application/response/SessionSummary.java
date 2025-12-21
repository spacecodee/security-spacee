package com.spacecodee.securityspacee.session.application.response;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import lombok.Builder;

@Builder
public record SessionSummary(
        @NonNull String sessionId,
        @NonNull String deviceName,
        @NonNull String location,
        @NonNull String ipAddress,
        @NonNull Instant createdAt,
        @NonNull Instant lastActivityAt,
        boolean isCurrent,
        boolean isTrustedDevice) {
}
