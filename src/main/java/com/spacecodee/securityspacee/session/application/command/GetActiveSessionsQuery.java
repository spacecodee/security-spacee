package com.spacecodee.securityspacee.session.application.command;

import org.jspecify.annotations.NonNull;

public record GetActiveSessionsQuery(@NonNull Integer userId, @NonNull String currentSessionId) {
}
