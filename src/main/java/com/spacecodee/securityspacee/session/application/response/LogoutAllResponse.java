package com.spacecodee.securityspacee.session.application.response;

import java.time.Instant;
import java.util.List;

import org.jspecify.annotations.NonNull;

import lombok.Builder;

@Builder
public record LogoutAllResponse(
        int sessionsLoggedOut,
        @NonNull String message,
        @NonNull Instant logoutAt,
        @NonNull List<String> devices) {
}
