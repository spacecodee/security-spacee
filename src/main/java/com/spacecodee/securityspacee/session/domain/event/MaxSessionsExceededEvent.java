package com.spacecodee.securityspacee.session.domain.event;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import lombok.Builder;

@Builder
public record MaxSessionsExceededEvent(
        @NonNull Integer userId,
        @NonNull String attemptedDeviceName,
        @NonNull String attemptedIpAddress,
        int currentActiveSessionsCount,
        int maxAllowed,
        @NonNull Instant deniedAt) {
}
