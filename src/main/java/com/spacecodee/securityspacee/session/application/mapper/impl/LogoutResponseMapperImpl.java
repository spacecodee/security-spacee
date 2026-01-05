package com.spacecodee.securityspacee.session.application.mapper.impl;

import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.spacecodee.securityspacee.session.application.mapper.ILogoutResponseMapper;
import com.spacecodee.securityspacee.session.application.response.LogoutResponse;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.valueobject.LogoutInfo;

public final class LogoutResponseMapperImpl implements ILogoutResponseMapper {

    private final MessageSource messageSource;

    public LogoutResponseMapperImpl(@NonNull MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public @NonNull LogoutResponse toResponse(@NonNull Session session) {
        Objects.requireNonNull(session, "session cannot be null");

        final LogoutInfo logoutInfo = session.getLogoutInfo();
        Objects.requireNonNull(logoutInfo, "logoutInfo cannot be null");

        final String message = this.messageSource.getMessage(
                "session.logout.success",
                null,
                LocaleContextHolder.getLocale());

        return LogoutResponse.builder()
                .message(message)
                .sessionId(session.getSessionId().getValue().toString())
                .logoutAt(logoutInfo.getLogoutAt())
                .build();
    }
}
