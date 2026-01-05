package com.spacecodee.securityspacee.session.application.response;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import lombok.Builder;

@Builder
public record LogoutResponse(
        @NonNull String message,
        @NonNull String sessionId,
        @NonNull Instant logoutAt) {
}
