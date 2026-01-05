package com.spacecodee.securityspacee.session.application.command;

import org.jspecify.annotations.NonNull;

import lombok.Builder;

@Builder
public record LogoutCommand(
        @NonNull String sessionId,
        @NonNull Integer userId,
        @NonNull String reason) {
}
