package com.spacecodee.securityspacee.session.application.command;

import org.jspecify.annotations.NonNull;

import lombok.Builder;

@Builder
public record CreateSessionCommand(
        @NonNull Integer userId,
        @NonNull String ipAddress,
        @NonNull String userAgent) {
}
