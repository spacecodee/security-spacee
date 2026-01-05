package com.spacecodee.securityspacee.session.application.usecase;

import java.time.Duration;
import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import com.spacecodee.securityspacee.session.application.command.UpdateSessionActivityCommand;
import com.spacecodee.securityspacee.session.application.port.in.IUpdateSessionActivityUseCase;
import com.spacecodee.securityspacee.session.domain.event.SessionActivityUpdatedEvent;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.service.ITimeoutPolicyService;
import com.spacecodee.securityspacee.session.domain.service.SessionExpirationService;
import com.spacecodee.securityspacee.session.domain.valueobject.TimeoutPolicy;

public final class SessionActivityUpdater implements IUpdateSessionActivityUseCase {

    private static final Logger log = LoggerFactory.getLogger(SessionActivityUpdater.class);

    private final ISessionRepository sessionRepository;
    private final ITimeoutPolicyService timeoutPolicyService;
    private final SessionExpirationService sessionExpirationService;
    private final ApplicationEventPublisher eventPublisher;

    public SessionActivityUpdater(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ITimeoutPolicyService timeoutPolicyService,
            @NonNull SessionExpirationService sessionExpirationService,
            @NonNull ApplicationEventPublisher eventPublisher) {
        this.sessionRepository = sessionRepository;
        this.timeoutPolicyService = timeoutPolicyService;
        this.sessionExpirationService = sessionExpirationService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(@NonNull UpdateSessionActivityCommand command) {
        this.sessionRepository.findById(command.sessionId()).ifPresentOrElse(
                session -> this.updateSessionActivity(session, command.timestamp()),
                () -> log.debug("Session {} not found, skipping activity update", command.sessionId()));
    }

    private void updateSessionActivity(@NonNull Session session, @NonNull Instant timestamp) {
        if (!session.isActive()) {
            log.debug("Session {} is not active, skipping activity update", session.getSessionId());
            return;
        }

        TimeoutPolicy policy = this.timeoutPolicyService.getPolicyForUser(session.getUserId());
        Duration idleTime = session.getIdleTime(timestamp);

        if (session.isIdleExpired(policy.getIdleTimeout(), timestamp)) {
            this.expireSessionByIdle(session, timestamp, idleTime);
            return;
        }

        Session updatedSession = session.updateLastActivityAt(timestamp);

        if (policy.isUseSlidingExpiration()) {
            Instant newExpiresAt = policy.calculateAbsoluteExpiration(timestamp);
            updatedSession = updatedSession.extendExpiration(newExpiresAt);
        }

        Session savedSession = this.sessionRepository.save(updatedSession);

        SessionActivityUpdatedEvent event = SessionActivityUpdatedEvent.builder()
                .sessionId(savedSession.getSessionId())
                .userId(savedSession.getUserId())
                .lastActivityAt(timestamp)
                .build();

        this.eventPublisher.publishEvent(event);
    }

    private void expireSessionByIdle(@NonNull Session session, @NonNull Instant timestamp, @NonNull Duration idleTime) {
        this.sessionExpirationService.expireByIdleTimeout(session, timestamp, idleTime);
    }
}
