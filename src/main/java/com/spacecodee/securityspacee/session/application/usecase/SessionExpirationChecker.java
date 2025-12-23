package com.spacecodee.securityspacee.session.application.usecase;

import java.time.Duration;
import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.command.CheckSessionExpirationQuery;
import com.spacecodee.securityspacee.session.application.port.in.ICheckSessionExpirationUseCase;
import com.spacecodee.securityspacee.session.application.response.SessionExpirationStatus;
import com.spacecodee.securityspacee.session.domain.exception.SessionNotFoundException;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.service.ITimeoutPolicyService;
import com.spacecodee.securityspacee.session.domain.valueobject.TimeoutPolicy;
import com.spacecodee.securityspacee.shared.application.port.out.IMessageResolverPort;

public final class SessionExpirationChecker implements ICheckSessionExpirationUseCase {

    private final ISessionRepository sessionRepository;
    private final ITimeoutPolicyService timeoutPolicyService;
    private final IMessageResolverPort messageResolverPort;

    public SessionExpirationChecker(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ITimeoutPolicyService timeoutPolicyService,
            @NonNull IMessageResolverPort messageResolverPort) {
        this.sessionRepository = sessionRepository;
        this.timeoutPolicyService = timeoutPolicyService;
        this.messageResolverPort = messageResolverPort;
    }

    @Override
    public @NonNull SessionExpirationStatus execute(@NonNull CheckSessionExpirationQuery query) {
        Session session = this.sessionRepository.findById(query.sessionId())
                .orElseThrow(SessionNotFoundException::new);

        Instant now = Instant.now();
        TimeoutPolicy policy = this.timeoutPolicyService.getPolicyForUser(session.getUserId());

        Duration remainingIdleTime = this.calculateRemainingIdleTime(session, policy, now);
        Duration remainingAbsoluteTime = session.getRemainingAbsoluteTime(now);

        boolean isExpired = !session.isActive() || session.isExpired();
        boolean shouldShowWarning = !isExpired && policy.shouldWarn(remainingAbsoluteTime);

        String warningMessage = shouldShowWarning
                ? this.buildWarningMessage(remainingIdleTime, remainingAbsoluteTime)
                : null;

        return SessionExpirationStatus.builder()
                .isExpired(isExpired)
                .remainingIdleTime(remainingIdleTime)
                .remainingAbsoluteTime(remainingAbsoluteTime)
                .shouldShowWarning(shouldShowWarning)
                .warningMessage(warningMessage)
                .build();
    }

    private @NonNull Duration calculateRemainingIdleTime(
            @NonNull Session session,
            @NonNull TimeoutPolicy policy,
            @NonNull Instant now) {
        Duration idleTime = session.getIdleTime(now);
        Duration remainingIdle = policy.getIdleTimeout().minus(idleTime);
        return remainingIdle.isNegative() ? Duration.ZERO : remainingIdle;
    }

    private @NonNull String buildWarningMessage(
            @NonNull Duration remainingIdleTime,
            @NonNull Duration remainingAbsoluteTime) {
        Duration smallerRemaining = remainingIdleTime.compareTo(remainingAbsoluteTime) < 0
                ? remainingIdleTime
                : remainingAbsoluteTime;

        long minutes = smallerRemaining.toMinutes();

        if (minutes > 0) {
            return this.messageResolverPort.getMessage("session.expiration.warning.minutes", minutes);
        }

        return this.messageResolverPort.getMessage("session.expiration.warning.soon");
    }
}
