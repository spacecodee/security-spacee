package com.spacecodee.securityspacee.jwttoken.infrastructure.session;

import java.time.Instant;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import com.spacecodee.securityspacee.jwttoken.application.port.out.ISessionValidationPort;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public final class SessionValidationAdapter implements ISessionValidationPort {

    private final ISessionRepository sessionRepository;

    @Override
    public @NonNull Optional<Session> findActiveSession(@NonNull SessionId sessionId) {
        return this.sessionRepository.findById(sessionId)
                .filter(Session::isActive);
    }

    @Override
    public boolean isSessionActive(@NonNull SessionId sessionId) {
        return this.sessionRepository.findById(sessionId)
                .map(Session::isActive)
                .orElse(false);
    }

    @Override
    public void updateSessionActivity(@NonNull SessionId sessionId, @NonNull Instant timestamp) {
        this.sessionRepository.findById(sessionId)
                .filter(Session::isActive)
                .ifPresent(session -> {
                    Session updatedSession = session.updateActivity();
                    this.sessionRepository.save(updatedSession);
                });
    }
}
