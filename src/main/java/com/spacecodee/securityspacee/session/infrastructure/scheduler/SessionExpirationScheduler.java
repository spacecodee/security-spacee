package com.spacecodee.securityspacee.session.infrastructure.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.service.ITimeoutPolicyService;
import com.spacecodee.securityspacee.session.domain.service.SessionExpirationService;
import com.spacecodee.securityspacee.session.domain.valueobject.TimeoutPolicy;

@Component
public class SessionExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(SessionExpirationScheduler.class);

    private final ISessionRepository sessionRepository;
    private final ITimeoutPolicyService timeoutPolicyService;
    private final SessionExpirationService sessionExpirationService;

    public SessionExpirationScheduler(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ITimeoutPolicyService timeoutPolicyService,
            @NonNull SessionExpirationService sessionExpirationService) {
        this.sessionRepository = sessionRepository;
        this.timeoutPolicyService = timeoutPolicyService;
        this.sessionExpirationService = sessionExpirationService;
    }

    @Scheduled(fixedDelayString = "${session.timeout.cleanup.idle-check-interval}")
    @Transactional
    public void expireInactiveSessions() {
        try {
            TimeoutPolicy defaultPolicy = this.timeoutPolicyService.getDefaultPolicy();
            Instant cutoff = Instant.now().minus(defaultPolicy.getIdleTimeout());

            List<Session> inactiveSessions = this.sessionRepository.findInactiveSessions(cutoff);

            if (inactiveSessions.isEmpty()) {
                return;
            }

            log.info("Found {} inactive sessions to expire", inactiveSessions.size());

            for (Session session : inactiveSessions) {
                this.expireSessionByIdle(session, Instant.now());
            }

            log.info("Expired {} inactive sessions", inactiveSessions.size());

        } catch (Exception e) {
            log.error("Error expiring inactive sessions", e);
        }
    }

    @Scheduled(fixedDelayString = "${session.timeout.cleanup.absolute-check-interval}")
    @Transactional
    public void expireAbsoluteTimeoutSessions() {
        try {
            Instant now = Instant.now();

            List<Session> expiredSessions = this.sessionRepository.findAbsoluteExpired(now);

            if (expiredSessions.isEmpty()) {
                return;
            }

            log.info("Found {} sessions with absolute timeout expired", expiredSessions.size());

            for (Session session : expiredSessions) {
                this.expireSessionByAbsoluteTimeout(session, now);
            }

            log.info("Expired {} sessions by absolute timeout", expiredSessions.size());

        } catch (Exception e) {
            log.error("Error expiring sessions by absolute timeout", e);
        }
    }

    private void expireSessionByIdle(@NonNull Session session, @NonNull Instant now) {
        Duration idleTime = session.getIdleTime(now);
        this.sessionExpirationService.expireByIdleTimeout(session, now, idleTime);
    }

    private void expireSessionByAbsoluteTimeout(@NonNull Session session, @NonNull Instant now) {
        this.sessionExpirationService.expireByAbsoluteTimeout(session, now);
    }
}
