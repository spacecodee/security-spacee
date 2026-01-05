package com.spacecodee.securityspacee.session.infrastructure.service;

import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.spacecodee.securityspacee.session.domain.exception.SessionAlreadyLoggedOutException;
import com.spacecodee.securityspacee.session.domain.exception.SessionNotFoundException;
import com.spacecodee.securityspacee.session.domain.exception.UnauthorizedSessionAccessException;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;

public class SessionValidationService {

    private final ISessionRepository sessionRepository;
    private final MessageSource messageSource;

    public SessionValidationService(
            @NonNull ISessionRepository sessionRepository,
            @NonNull MessageSource messageSource) {
        this.sessionRepository = sessionRepository;
        this.messageSource = messageSource;
    }

    public @NonNull Session validateAndGetSessionForLogout(
            @NonNull String sessionIdStr,
            @NonNull Integer userId) {
        final SessionId sessionId = SessionId.parse(sessionIdStr);

        final Session session = this.sessionRepository.findById(sessionId)
                .orElseThrow(() -> {
                    final String message = this.messageSource.getMessage(
                            "session.error.not_found",
                            new Object[]{sessionIdStr},
                            LocaleContextHolder.getLocale());
                    return new SessionNotFoundException(message);
                });

        if (!session.isOwnedBy(userId)) {
            final String message = this.messageSource.getMessage(
                    "session.error.unauthorized_access",
                    null,
                    LocaleContextHolder.getLocale());
            throw new UnauthorizedSessionAccessException(message);
        }

        if (!session.isActive()) {
            final String message = this.messageSource.getMessage(
                    "session.error.already_logged_out",
                    new Object[]{session.getState()},
                    LocaleContextHolder.getLocale());
            throw new SessionAlreadyLoggedOutException(message);
        }

        return session;
    }
}
