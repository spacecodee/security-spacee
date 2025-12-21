package com.spacecodee.securityspacee.session.application.command;

import org.jspecify.annotations.NonNull;

public record LogoutRemoteSessionCommand(
        @NonNull Integer userId,
        @NonNull String targetSessionId,
        @NonNull String reason) {
}
