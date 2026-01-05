package com.spacecodee.securityspacee.session.application.usecase;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;

import com.spacecodee.securityspacee.session.application.command.LogoutCommand;
import com.spacecodee.securityspacee.session.application.mapper.ILogoutResponseMapper;
import com.spacecodee.securityspacee.session.application.port.in.ILogoutUseCase;
import com.spacecodee.securityspacee.session.application.response.LogoutResponse;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.infrastructure.event.SessionEventPublisher;
import com.spacecodee.securityspacee.session.infrastructure.service.SessionValidationService;

public class LogoutUseCase implements ILogoutUseCase {

    private final ILogoutResponseMapper logoutResponseMapper;
    private final SessionEventPublisher sessionEventPublisher;
    private final ISessionRepository sessionRepository;
    private final SessionValidationService sessionValidationService;

    public LogoutUseCase(
            @NonNull ILogoutResponseMapper logoutResponseMapper,
            @NonNull SessionEventPublisher sessionEventPublisher,
            @NonNull ISessionRepository sessionRepository,
            @NonNull SessionValidationService sessionValidationService) {
        this.logoutResponseMapper = logoutResponseMapper;
        this.sessionEventPublisher = sessionEventPublisher;
        this.sessionRepository = sessionRepository;
        this.sessionValidationService = sessionValidationService;
    }

    @Override
    @Transactional
    public @NonNull LogoutResponse execute(@NonNull LogoutCommand command) {
        final Session session = this.sessionValidationService.validateAndGetSessionForLogout(
                command.sessionId(),
                command.userId());

        final Instant logoutAt = Instant.now();
        final Session loggedOutSession = session.logout(command.reason(), logoutAt, command.userId());
        this.sessionRepository.save(loggedOutSession);

        this.sessionEventPublisher.publishSessionLoggedOutEvent(loggedOutSession, false);

        return this.logoutResponseMapper.toResponse(loggedOutSession);
    }
}
