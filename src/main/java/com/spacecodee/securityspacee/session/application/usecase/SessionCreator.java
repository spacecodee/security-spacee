package com.spacecodee.securityspacee.session.application.usecase;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.spacecodee.securityspacee.session.application.command.CreateSessionCommand;
import com.spacecodee.securityspacee.session.application.mapper.ISessionResponseMapper;
import com.spacecodee.securityspacee.session.application.port.in.ICreateSessionUseCase;
import com.spacecodee.securityspacee.session.application.port.out.IDeviceFingerprintService;
import com.spacecodee.securityspacee.session.application.port.out.IGeoIpService;
import com.spacecodee.securityspacee.session.application.port.out.IUserAgentParser;
import com.spacecodee.securityspacee.session.application.response.SessionResponse;
import com.spacecodee.securityspacee.session.domain.event.MaxSessionsExceededEvent;
import com.spacecodee.securityspacee.session.domain.event.NewDeviceLoginEvent;
import com.spacecodee.securityspacee.session.domain.event.SessionCreatedEvent;
import com.spacecodee.securityspacee.session.domain.event.SessionForcedLogoutEvent;
import com.spacecodee.securityspacee.session.domain.exception.InvalidSessionStateException;
import com.spacecodee.securityspacee.session.domain.exception.MaxSessionsExceededException;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.service.ISessionPolicyService;
import com.spacecodee.securityspacee.session.domain.valueobject.ConcurrentSessionPolicy;
import com.spacecodee.securityspacee.session.domain.valueobject.DeviceFingerprint;
import com.spacecodee.securityspacee.session.domain.valueobject.DeviceInfo;
import com.spacecodee.securityspacee.session.domain.valueobject.Location;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionExpirationStrategy;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionMetadata;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionPolicy;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionState;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionToken;
import com.spacecodee.securityspacee.session.infrastructure.config.properties.SessionProperties;

public final class SessionCreator implements ICreateSessionUseCase {

    private final ISessionRepository sessionRepository;
    private final ISessionResponseMapper responseMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final SessionProperties sessionProperties;
    private final ISessionPolicyService sessionPolicyService;
    private final IDeviceFingerprintService deviceFingerprintService;
    private final IUserAgentParser userAgentParser;
    private final IGeoIpService geoIpService;
    private final MessageSource messageSource;

    public SessionCreator(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ISessionResponseMapper responseMapper,
            @NonNull ApplicationEventPublisher eventPublisher,
            @NonNull SessionProperties sessionProperties,
            @NonNull ISessionPolicyService sessionPolicyService,
            @NonNull IDeviceFingerprintService deviceFingerprintService,
            @NonNull IUserAgentParser userAgentParser,
            @NonNull IGeoIpService geoIpService,
            @NonNull MessageSource messageSource) {
        this.sessionRepository = sessionRepository;
        this.responseMapper = responseMapper;
        this.eventPublisher = eventPublisher;
        this.sessionProperties = sessionProperties;
        this.sessionPolicyService = sessionPolicyService;
        this.deviceFingerprintService = deviceFingerprintService;
        this.userAgentParser = userAgentParser;
        this.geoIpService = geoIpService;
        this.messageSource = messageSource;
    }

    @Override
    public @NonNull SessionResponse execute(@NonNull CreateSessionCommand command) {
        final SessionPolicy policy = this.sessionPolicyService.getPolicyForUser(command.userId());

        if (policy.type() != ConcurrentSessionPolicy.UNLIMITED) {
            this.handleSessionLimit(command.userId(), policy, command.ipAddress(), command.userAgent());
        }

        final DeviceFingerprint fingerprint = this.deviceFingerprintService.generate(
                command.ipAddress(),
                command.userAgent());

        final boolean isTrustedDevice = this.sessionRepository.existsByUserIdAndFingerprint(
                command.userId(),
                fingerprint);

        final DeviceInfo deviceInfo = this.userAgentParser.parse(command.userAgent());
        final String deviceName = deviceInfo.friendlyName();

        final Optional<Location> locationOpt = this.geoIpService.lookup(command.ipAddress());
        final Location location = locationOpt.orElse(null);

        final Instant now = Instant.now();
        final Instant expiresAt = now.plus(this.sessionProperties.expiration());

        final SessionMetadata metadata = SessionMetadata.builder()
                .ipAddress(command.ipAddress())
                .userAgent(command.userAgent())
                .deviceFingerprint(fingerprint)
                .deviceName(deviceName)
                .location(location)
                .isTrustedDevice(isTrustedDevice)
                .createdAt(now)
                .expiresAt(expiresAt)
                .lastActivityAt(now)
                .build();

        final SessionId sessionId = SessionId.generate();
        final SessionToken sessionToken = SessionToken.generate();

        final Session session = Session.builder()
                .sessionId(sessionId)
                .sessionToken(sessionToken)
                .userId(command.userId())
                .metadata(metadata)
                .state(SessionState.ACTIVE)
                .logoutInfo(null)
                .build();

        final Session savedSession = this.sessionRepository.save(session);

        this.publishSessionCreatedEvent(savedSession);

        if (!isTrustedDevice && policy.notifyOnNewDevice()) {
            this.publishNewDeviceLoginEvent(savedSession);
        }

        return this.responseMapper.toResponse(savedSession);
    }

    private void handleSessionLimit(@NonNull Integer userId, @NonNull SessionPolicy policy,
                                    @NonNull String ipAddress, @NonNull String userAgent) {
        final List<Session> activeSessions = this.sessionRepository.findActiveByUserId(userId);
        final int activeCount = activeSessions.size();

        if (activeCount >= policy.maxSessions()) {
            if (policy.expirationStrategy() == SessionExpirationStrategy.EXPLICIT_LOGOUT) {
                this.publishMaxSessionsExceededEvent(userId, ipAddress, userAgent, activeCount, policy.maxSessions());
                final String message = this.messageSource.getMessage(
                        "session.exception.max_sessions_exceeded",
                        new Object[]{policy.maxSessions()},
                        LocaleContextHolder.getLocale());
                throw new MaxSessionsExceededException(message);
            }

            this.expireOldestSession(activeSessions, policy.expirationStrategy());
        }
    }

    private void expireOldestSession(@NonNull List<Session> activeSessions,
                                     @NonNull SessionExpirationStrategy strategy) {
        final Session sessionToExpire = switch (strategy) {
            case FIFO -> activeSessions.stream()
                    .min(Comparator.comparing(s -> s.getMetadata().getCreatedAt()))
                    .orElseThrow();
            case LEAST_USED -> activeSessions.stream()
                    .min(Comparator.comparing(s -> s.getMetadata().getLastActivityAt()))
                    .orElseThrow();
            default -> {
                final String errorMessage = this.messageSource.getMessage(
                        "session.error.unsupported_expiration_strategy",
                        new Object[]{strategy},
                        LocaleContextHolder.getLocale());
                throw new InvalidSessionStateException(errorMessage);
            }
        };

        final Instant now = Instant.now();
        final Session expiredSession = sessionToExpire.forceLogout("session_limit_reached", now);
        this.sessionRepository.save(expiredSession);

        this.publishSessionForcedLogoutEvent(expiredSession, now);
    }

    private void publishSessionCreatedEvent(@NonNull Session session) {
        final SessionCreatedEvent event = SessionCreatedEvent.builder()
                .sessionId(session.getSessionId())
                .sessionToken(session.getSessionToken())
                .userId(session.getUserId())
                .createdAt(session.getMetadata().getCreatedAt())
                .expiresAt(session.getMetadata().getExpiresAt())
                .build();

        this.eventPublisher.publishEvent(event);
    }

    private void publishNewDeviceLoginEvent(@NonNull Session session) {
        final String deviceName = session.getMetadata().getDeviceName() != null
                ? session.getMetadata().getDeviceName()
                : "Unknown Device";

        final NewDeviceLoginEvent event = NewDeviceLoginEvent.builder()
                .userId(session.getUserId())
                .sessionId(session.getSessionId().getValue().toString())
                .deviceName(deviceName)
                .location(session.getMetadata().getLocation())
                .ipAddress(session.getMetadata().getIpAddress())
                .isTrustedDevice(session.getMetadata().isTrustedDevice())
                .loginAt(session.getMetadata().getCreatedAt())
                .build();

        this.eventPublisher.publishEvent(event);
    }

    private void publishSessionForcedLogoutEvent(@NonNull Session session, @NonNull Instant forcedAt) {
        final String deviceName = session.getMetadata().getDeviceName() != null
                ? session.getMetadata().getDeviceName()
                : "Unknown Device";

        final SessionForcedLogoutEvent event = SessionForcedLogoutEvent.builder()
                .sessionId(session.getSessionId().getValue().toString())
                .userId(session.getUserId())
                .reason("session_limit_reached")
                .forcedAt(forcedAt)
                .replacedBy(null)
                .deviceName(deviceName)
                .build();

        this.eventPublisher.publishEvent(event);
    }

    private void publishMaxSessionsExceededEvent(@NonNull Integer userId, @NonNull String ipAddress,
                                                 @NonNull String userAgent, int currentCount, int maxAllowed) {
        final DeviceInfo deviceInfo = this.userAgentParser.parse(userAgent);

        final MaxSessionsExceededEvent event = MaxSessionsExceededEvent.builder()
                .userId(userId)
                .attemptedDeviceName(deviceInfo.friendlyName())
                .attemptedIpAddress(ipAddress)
                .currentActiveSessionsCount(currentCount)
                .maxAllowed(maxAllowed)
                .deniedAt(Instant.now())
                .build();

        this.eventPublisher.publishEvent(event);
    }
}
