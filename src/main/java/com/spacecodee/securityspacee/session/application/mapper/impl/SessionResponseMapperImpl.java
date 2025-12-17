package com.spacecodee.securityspacee.session.application.mapper.impl;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.mapper.ISessionResponseMapper;
import com.spacecodee.securityspacee.session.application.response.SessionResponse;
import com.spacecodee.securityspacee.session.domain.model.Session;

public final class SessionResponseMapperImpl implements ISessionResponseMapper {

    @Override
    public @NonNull SessionResponse toResponse(@NonNull Session session) {
        var logoutInfo = session.getLogoutInfo();

        return SessionResponse.builder()
                .sessionId(session.getSessionId().toString())
                .sessionToken(session.getSessionToken().toString())
                .userId(session.getUserId())
                .ipAddress(session.getMetadata().getIpAddress())
                .userAgent(session.getMetadata().getUserAgent())
                .createdAt(session.getMetadata().getCreatedAt())
                .expiresAt(session.getMetadata().getExpiresAt())
                .lastActivityAt(session.getMetadata().getLastActivityAt())
                .state(session.getState())
                .logoutAt(logoutInfo != null ? logoutInfo.getLogoutAt() : null)
                .logoutReason(logoutInfo != null ? logoutInfo.getLogoutReason() : null)
                .build();
    }
}
