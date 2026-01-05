package com.spacecodee.securityspacee.session.application.usecase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.spacecodee.securityspacee.session.application.command.LogoutAllCommand;
import com.spacecodee.securityspacee.session.application.mapper.ILogoutAllResponseMapper;
import com.spacecodee.securityspacee.session.application.port.in.ILogoutAllSessionsUseCase;
import com.spacecodee.securityspacee.session.application.response.LogoutAllResponse;
import com.spacecodee.securityspacee.session.domain.event.AllSessionsLoggedOutEvent;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.infrastructure.event.SessionEventPublisher;

public class LogoutAllSessionsUseCase implements ILogoutAllSessionsUseCase {

    private final ISessionRepository sessionRepository;
    private final ILogoutAllResponseMapper logoutAllResponseMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final SessionEventPublisher sessionEventPublisher;
    private final MessageSource messageSource;

    public LogoutAllSessionsUseCase(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ILogoutAllResponseMapper logoutAllResponseMapper,
            @NonNull SessionEventPublisher sessionEventPublisher,
            @NonNull MessageSource messageSource) {
        this.sessionRepository = sessionRepository;
        this.logoutAllResponseMapper = logoutAllResponseMapper;
        this.eventPublisher = sessionEventPublisher.getEventPublisher();
        this.sessionEventPublisher = sessionEventPublisher;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
    public @NonNull LogoutAllResponse execute(@NonNull LogoutAllCommand command) {
        final List<Session> activeSessions = this.sessionRepository.findActiveByUserId(command.userId());

        if (activeSessions.isEmpty()) {
            final String noSessionsMessage = this.messageSource.getMessage(
                    "session.logout.no_active_sessions",
                    null,
                    LocaleContextHolder.getLocale());
            return this.logoutAllResponseMapper.toResponse(0, List.of(), Instant.now(), noSessionsMessage);
        }

        final Instant logoutAt = Instant.now();
        final List<String> devices = new ArrayList<>();

        for (Session session : activeSessions) {
            final Session loggedOutSession = session.logout(command.reason(), logoutAt, command.userId());
            this.sessionRepository.save(loggedOutSession);

            devices.add(session.getMetadata().getDeviceName());

            this.sessionEventPublisher.publishSessionLoggedOutEvent(loggedOutSession, false);
        }

        this.publishAllSessionsLoggedOutEvent(command, activeSessions.size(), logoutAt);

        final String successMessage = this.messageSource.getMessage(
                "session.logout.all_sessions_closed",
                null,
                LocaleContextHolder.getLocale());

        return this.logoutAllResponseMapper.toResponse(activeSessions.size(), devices, logoutAt, successMessage);
    }

    private void publishAllSessionsLoggedOutEvent(@NonNull LogoutAllCommand command, int sessionCount,
                                                  @NonNull Instant loggedOutAt) {
        final AllSessionsLoggedOutEvent event = AllSessionsLoggedOutEvent.builder()
                .userId(command.userId())
                .sessionCount(sessionCount)
                .loggedOutAt(loggedOutAt)
                .reason(command.reason())
                .loggedOutBy(command.userId())
                .build();

        this.eventPublisher.publishEvent(event);
    }
}
