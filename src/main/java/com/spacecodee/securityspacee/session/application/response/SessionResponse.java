package com.spacecodee.securityspacee.session.application.response;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.spacecodee.securityspacee.session.domain.valueobject.LogoutReason;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionState;

import lombok.Builder;

@Builder
public record SessionResponse(
        @NonNull String sessionId,
        @NonNull String sessionToken,
        @NonNull Integer userId,
        @NonNull String ipAddress,
        @NonNull String userAgent,
        @NonNull Instant createdAt,
        @NonNull Instant expiresAt,
        @NonNull Instant lastActivityAt,
        @NonNull SessionState state,
        @Nullable Instant logoutAt,
        @Nullable LogoutReason logoutReason) {
}
