package com.spacecodee.securityspacee.session.domain.service;

import java.time.Duration;
import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import com.spacecodee.securityspacee.session.domain.event.SessionExpiredByAbsoluteTimeoutEvent;
import com.spacecodee.securityspacee.session.domain.event.SessionExpiredByIdleEvent;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;

/**
 * Domain Service for handling session expiration events.
 * Centralizes the logic for expiring sessions and publishing corresponding
 * domain events.
 */
public final class SessionExpirationService {

    private static final Logger log = LoggerFactory.getLogger(SessionExpirationService.class);

    private final ISessionRepository sessionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SessionExpirationService(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ApplicationEventPublisher eventPublisher) {
        this.sessionRepository = sessionRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Expires a session due to idle timeout and publishes the corresponding event.
     *
     * @param session   the session to expire
     * @param timestamp the timestamp when expiration occurs
     * @param idleTime  the duration of inactivity
     */
    public void expireByIdleTimeout(@NonNull Session session, @NonNull Instant timestamp,
                                    @NonNull Duration idleTime) {
        Session expiredSession = session.expireByTimeout("idle_timeout_exceeded", timestamp);
        Session savedSession = this.sessionRepository.save(expiredSession);

        SessionExpiredByIdleEvent event = SessionExpiredByIdleEvent.builder()
                .sessionId(savedSession.getSessionId().getValue().toString())
                .userId(savedSession.getUserId())
                .lastActivityAt(savedSession.getMetadata().getLastActivityAt())
                .expiredAt(timestamp)
                .idleTime(idleTime)
                .reason("idle_timeout")
                .build();

        this.eventPublisher.publishEvent(event);

        log.debug("Session {} expired due to idle timeout of {}", savedSession.getSessionId(), idleTime);
    }

    /**
     * Expires a session due to absolute timeout and publishes the corresponding
     * event.
     *
     * @param session   the session to expire
     * @param timestamp the timestamp when expiration occurs
     */
    public void expireByAbsoluteTimeout(@NonNull Session session, @NonNull Instant timestamp) {
        Duration sessionDuration = Duration.between(session.getMetadata().getCreatedAt(), timestamp);

        Session expiredSession = session.expireByTimeout("absolute_timeout_exceeded", timestamp);
        Session savedSession = this.sessionRepository.save(expiredSession);

        SessionExpiredByAbsoluteTimeoutEvent event = SessionExpiredByAbsoluteTimeoutEvent.builder()
                .sessionId(savedSession.getSessionId().getValue().toString())
                .userId(savedSession.getUserId())
                .createdAt(savedSession.getMetadata().getCreatedAt())
                .expiredAt(timestamp)
                .sessionDuration(sessionDuration)
                .reason("absolute_timeout")
                .build();

        this.eventPublisher.publishEvent(event);

        log.debug("Session {} expired due to absolute timeout after {}", savedSession.getSessionId(),
                sessionDuration);
    }
}
