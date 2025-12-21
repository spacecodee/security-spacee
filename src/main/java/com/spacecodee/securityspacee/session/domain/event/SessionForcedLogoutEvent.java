package com.spacecodee.securityspacee.session.domain.event;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import lombok.Builder;

@Builder
public record SessionForcedLogoutEvent(
        @NonNull String sessionId,
        @NonNull Integer userId,
        @NonNull String reason,
        @NonNull Instant forcedAt,
        @Nullable String replacedBy,
        @Nullable String deviceName) {
}
