package com.spacecodee.securityspacee.session.application.usecase;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;

import com.spacecodee.securityspacee.session.application.command.UpdateSessionActivityCommand;
import com.spacecodee.securityspacee.session.application.mapper.ISessionResponseMapper;
import com.spacecodee.securityspacee.session.application.port.in.IUpdateSessionActivityUseCase;
import com.spacecodee.securityspacee.session.application.response.SessionResponse;
import com.spacecodee.securityspacee.session.domain.event.SessionActivityUpdatedEvent;
import com.spacecodee.securityspacee.session.domain.exception.SessionNotFoundException;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionToken;

public final class SessionActivityUpdater implements IUpdateSessionActivityUseCase {

    private final ISessionRepository sessionRepository;
    private final ISessionResponseMapper responseMapper;
    private final ApplicationEventPublisher eventPublisher;

    public SessionActivityUpdater(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ISessionResponseMapper responseMapper,
            @NonNull ApplicationEventPublisher eventPublisher) {
        this.sessionRepository = sessionRepository;
        this.responseMapper = responseMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public @NonNull SessionResponse execute(@NonNull UpdateSessionActivityCommand command) {
        SessionToken sessionToken = SessionToken.parse(command.sessionToken());

        Session session = this.sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(SessionNotFoundException::new);

        Session updatedSession = session.updateActivity();
        Session savedSession = this.sessionRepository.save(updatedSession);

        SessionActivityUpdatedEvent event = SessionActivityUpdatedEvent.builder()
                .sessionId(savedSession.getSessionId())
                .userId(savedSession.getUserId())
                .lastActivityAt(Instant.now())
                .build();

        this.eventPublisher.publishEvent(event);

        return this.responseMapper.toResponse(savedSession);
    }
}
