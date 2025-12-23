package com.spacecodee.securityspacee.session.application.command;

import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;

public record CheckSessionExpirationQuery(@NonNull SessionId sessionId) {

    public CheckSessionExpirationQuery {
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
    }
}
