package com.spacecodee.securityspacee.session.application.mapper.impl;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.mapper.ILogoutAllResponseMapper;
import com.spacecodee.securityspacee.session.application.response.LogoutAllResponse;

public final class LogoutAllResponseMapperImpl implements ILogoutAllResponseMapper {

    @Override
    public @NonNull LogoutAllResponse toResponse(int sessionsLoggedOut, @NonNull List<String> devices,
                                                 @NonNull Instant logoutAt, @NonNull String message) {
        Objects.requireNonNull(devices, "devices cannot be null");
        Objects.requireNonNull(logoutAt, "logoutAt cannot be null");
        Objects.requireNonNull(message, "message cannot be null");

        return LogoutAllResponse.builder()
                .sessionsLoggedOut(sessionsLoggedOut)
                .devices(devices)
                .logoutAt(logoutAt)
                .message(message)
                .build();
    }
}
