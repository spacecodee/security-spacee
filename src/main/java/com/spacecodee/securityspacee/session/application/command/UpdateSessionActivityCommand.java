package com.spacecodee.securityspacee.session.application.command;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;

public record UpdateSessionActivityCommand(
        @NonNull SessionId sessionId,
        @NonNull Instant timestamp) {

    public UpdateSessionActivityCommand {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");
    }
}
