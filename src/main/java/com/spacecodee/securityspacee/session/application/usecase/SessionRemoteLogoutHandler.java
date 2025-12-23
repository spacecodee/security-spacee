package com.spacecodee.securityspacee.session.application.usecase;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.spacecodee.securityspacee.session.application.command.LogoutRemoteSessionCommand;
import com.spacecodee.securityspacee.session.application.port.in.ILogoutRemoteSessionUseCase;
import com.spacecodee.securityspacee.session.domain.event.SessionForcedLogoutEvent;
import com.spacecodee.securityspacee.session.domain.exception.SessionNotFoundException;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;

public final class SessionRemoteLogoutHandler implements ILogoutRemoteSessionUseCase {

    private final ISessionRepository sessionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MessageSource messageSource;

    public SessionRemoteLogoutHandler(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ApplicationEventPublisher eventPublisher,
            @NonNull MessageSource messageSource) {
        this.sessionRepository = sessionRepository;
        this.eventPublisher = eventPublisher;
        this.messageSource = messageSource;
    }

    @Override
    public void execute(@NonNull LogoutRemoteSessionCommand command) {
        final SessionId targetSessionId = SessionId.parse(command.targetSessionId());

        final Session session = this.sessionRepository.findById(targetSessionId)
                .orElseThrow(() -> {
                    final String message = this.messageSource.getMessage(
                            "session.exception.not_found",
                            new Object[]{command.targetSessionId()},
                            LocaleContextHolder.getLocale());
                    return new SessionNotFoundException(message);
                });

        if (!session.getUserId().equals(command.userId())) {
            final String message = this.messageSource.getMessage(
                    "session.exception.unauthorized_logout",
                    null,
                    LocaleContextHolder.getLocale());
            throw new SecurityException(message);
        }

        final Instant now = Instant.now();
        final Session loggedOutSession = session.forceLogout(command.reason(), now);
        this.sessionRepository.save(loggedOutSession);

        this.publishSessionForcedLogoutEvent(loggedOutSession, now);
    }

    private void publishSessionForcedLogoutEvent(@NonNull Session session, @NonNull Instant forcedAt) {
        final String deviceName = session.getMetadata().getDeviceName() != null
                ? session.getMetadata().getDeviceName()
                : "Unknown Device";

        final SessionForcedLogoutEvent event = SessionForcedLogoutEvent.builder()
                .sessionId(session.getSessionId().getValue().toString())
                .userId(session.getUserId())
                .reason("manual_logout_from_ui")
                .forcedAt(forcedAt)
                .replacedBy(null)
                .deviceName(deviceName)
                .build();

        this.eventPublisher.publishEvent(event);
    }
}
