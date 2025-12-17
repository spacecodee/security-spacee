package com.spacecodee.securityspacee.session.application.usecase;

import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;

import com.spacecodee.securityspacee.session.application.command.LogoutSessionCommand;
import com.spacecodee.securityspacee.session.application.port.in.ILogoutSessionUseCase;
import com.spacecodee.securityspacee.session.domain.event.SessionLoggedOutEvent;
import com.spacecodee.securityspacee.session.domain.exception.SessionNotFoundException;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionToken;

public final class SessionLogoutHandler implements ILogoutSessionUseCase {

    private final ISessionRepository sessionRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SessionLogoutHandler(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ApplicationEventPublisher eventPublisher) {
        this.sessionRepository = sessionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void execute(@NonNull LogoutSessionCommand command) {
        SessionToken sessionToken = SessionToken.parse(command.sessionToken());

        Session session = this.sessionRepository.findBySessionToken(sessionToken)
                .orElseThrow(SessionNotFoundException::new);

        Session loggedOutSession = session.logout(command.logoutReason());
        Session savedSession = this.sessionRepository.save(loggedOutSession);

        var logoutInfo = savedSession.getLogoutInfo();
        if (logoutInfo == null) {
            throw new IllegalStateException("session.error.logout_info_null");
        }

        SessionLoggedOutEvent event = SessionLoggedOutEvent.builder()
                .sessionId(savedSession.getSessionId())
                .userId(savedSession.getUserId())
                .logoutReason(logoutInfo.getLogoutReason())
                .loggedOutAt(logoutInfo.getLogoutAt())
                .build();

        this.eventPublisher.publishEvent(event);
    }
}
