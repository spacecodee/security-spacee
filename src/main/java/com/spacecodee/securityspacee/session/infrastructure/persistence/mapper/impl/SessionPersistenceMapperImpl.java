package com.spacecodee.securityspacee.session.infrastructure.persistence.mapper.impl;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.valueobject.LogoutInfo;
import com.spacecodee.securityspacee.session.domain.valueobject.LogoutReason;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionMetadata;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionState;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionToken;
import com.spacecodee.securityspacee.session.infrastructure.persistence.jpa.LogoutReasonDb;
import com.spacecodee.securityspacee.session.infrastructure.persistence.jpa.SessionEntity;
import com.spacecodee.securityspacee.session.infrastructure.persistence.mapper.ISessionPersistenceMapper;

public final class SessionPersistenceMapperImpl implements ISessionPersistenceMapper {

    @Override
    public @NonNull SessionEntity toEntity(@NonNull Session session) {
        var logoutInfo = session.getLogoutInfo();
        boolean isActive = session.getState() == SessionState.ACTIVE;

        return SessionEntity.builder()
                .sessionId(session.getSessionId().toString())
                .userId(session.getUserId())
                .sessionToken(session.getSessionToken().toString())
                .ipAddress(session.getMetadata().getIpAddress())
                .userAgent(session.getMetadata().getUserAgent())
                .createdAt(session.getMetadata().getCreatedAt())
                .expiresAt(session.getMetadata().getExpiresAt())
                .lastActivityAt(session.getMetadata().getLastActivityAt())
                .isActive(isActive)
                .logoutAt(logoutInfo != null ? logoutInfo.getLogoutAt() : null)
                .logoutReason(logoutInfo != null ? this.toLogoutReasonDb(logoutInfo.getLogoutReason()) : null)
                .build();
    }

    @Override
    public @NonNull Session toDomain(@NonNull SessionEntity entity) {
        SessionMetadata metadata = SessionMetadata.builder()
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .lastActivityAt(entity.getLastActivityAt())
                .build();

        LogoutInfo logoutInfo = null;
        if (entity.getLogoutAt() != null && entity.getLogoutReason() != null) {
            logoutInfo = LogoutInfo.builder()
                    .logoutAt(entity.getLogoutAt())
                    .logoutReason(this.toLogoutReason(entity.getLogoutReason()))
                    .build();
        }

        SessionState state = this.toState(entity.getIsActive(), entity.getLogoutAt(), entity.getLogoutReason());

        return Session.builder()
                .sessionId(SessionId.parse(entity.getSessionId()))
                .sessionToken(SessionToken.parse(entity.getSessionToken()))
                .userId(entity.getUserId())
                .metadata(metadata)
                .state(state)
                .logoutInfo(logoutInfo)
                .build();
    }

    private @NonNull SessionState toState(@NonNull Boolean isActive, Instant logoutAt, LogoutReasonDb logoutReason) {
        if (logoutAt != null) {
            if (logoutReason == LogoutReasonDb.FORCED) {
                return SessionState.FORCED_LOGOUT;
            }
            return SessionState.LOGGED_OUT;
        }

        if (!isActive) {
            return SessionState.EXPIRED;
        }

        return SessionState.ACTIVE;
    }

    private @NonNull LogoutReasonDb toLogoutReasonDb(@NonNull LogoutReason reason) {
        return switch (reason) {
            case MANUAL -> LogoutReasonDb.MANUAL;
            case TIMEOUT -> LogoutReasonDb.TIMEOUT;
            case FORCED -> LogoutReasonDb.FORCED;
            case SECURITY -> LogoutReasonDb.SECURITY;
        };
    }

    private @NonNull LogoutReason toLogoutReason(@NonNull LogoutReasonDb reasonDb) {
        return switch (reasonDb) {
            case MANUAL -> LogoutReason.MANUAL;
            case TIMEOUT -> LogoutReason.TIMEOUT;
            case FORCED -> LogoutReason.FORCED;
            case SECURITY -> LogoutReason.SECURITY;
        };
    }
}
