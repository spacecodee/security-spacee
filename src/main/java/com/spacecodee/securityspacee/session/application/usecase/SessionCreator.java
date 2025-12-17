package com.spacecodee.securityspacee.session.application.usecase;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;

import com.spacecodee.securityspacee.session.application.command.CreateSessionCommand;
import com.spacecodee.securityspacee.session.application.mapper.ISessionResponseMapper;
import com.spacecodee.securityspacee.session.application.port.in.ICreateSessionUseCase;
import com.spacecodee.securityspacee.session.application.response.SessionResponse;
import com.spacecodee.securityspacee.session.domain.event.SessionCreatedEvent;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionMetadata;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionState;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionToken;
import com.spacecodee.securityspacee.session.infrastructure.config.properties.SessionProperties;

public final class SessionCreator implements ICreateSessionUseCase {

    private final ISessionRepository sessionRepository;
    private final ISessionResponseMapper responseMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final SessionProperties sessionProperties;

    public SessionCreator(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ISessionResponseMapper responseMapper,
            @NonNull ApplicationEventPublisher eventPublisher,
            @NonNull SessionProperties sessionProperties) {
        this.sessionRepository = sessionRepository;
        this.responseMapper = responseMapper;
        this.eventPublisher = eventPublisher;
        this.sessionProperties = sessionProperties;
    }

    @Override
    public @NonNull SessionResponse execute(@NonNull CreateSessionCommand command) {
        SessionId sessionId = SessionId.generate();
        SessionToken sessionToken = SessionToken.generate();
        Instant now = Instant.now();
        Instant expiresAt = now.plus(this.sessionProperties.expiration());

        SessionMetadata metadata = SessionMetadata.builder()
                .ipAddress(command.ipAddress())
                .userAgent(command.userAgent())
                .createdAt(now)
                .expiresAt(expiresAt)
                .lastActivityAt(now)
                .build();

        Session session = Session.builder()
                .sessionId(sessionId)
                .sessionToken(sessionToken)
                .userId(command.userId())
                .metadata(metadata)
                .state(SessionState.ACTIVE)
                .logoutInfo(null)
                .build();

        Session savedSession = this.sessionRepository.save(session);

        SessionCreatedEvent event = SessionCreatedEvent.builder()
                .sessionId(savedSession.getSessionId())
                .sessionToken(savedSession.getSessionToken())
                .userId(savedSession.getUserId())
                .createdAt(savedSession.getMetadata().getCreatedAt())
                .expiresAt(savedSession.getMetadata().getExpiresAt())
                .build();

        this.eventPublisher.publishEvent(event);

        return this.responseMapper.toResponse(savedSession);
    }
}
