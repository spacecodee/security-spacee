package com.spacecodee.securityspacee.jwttoken.application.port.out;

import java.time.Instant;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;

public interface ISessionValidationPort {

    @NonNull
    Optional<Session> findActiveSession(@NonNull SessionId sessionId);

    boolean isSessionActive(@NonNull SessionId sessionId);

    void updateSessionActivity(@NonNull SessionId sessionId, @NonNull Instant timestamp);
}
