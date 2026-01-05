package com.spacecodee.securityspacee.session.infrastructure.persistence.mapper.impl;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.valueobject.DeviceFingerprint;
import com.spacecodee.securityspacee.session.domain.valueobject.Location;
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

        final Location location = session.getMetadata().getLocation();
        final DeviceFingerprint fingerprint = session.getMetadata().getDeviceFingerprint();

        return SessionEntity.builder()
                .sessionId(session.getSessionId().toString())
                .userId(session.getUserId())
                .sessionToken(session.getSessionToken().toString())
                .ipAddress(session.getMetadata().getIpAddress())
                .userAgent(session.getMetadata().getUserAgent())
                .createdAt(session.getMetadata().getCreatedAt())
                .expiresAt(session.getMetadata().getExpiresAt())
                .lastActivityAt(session.getMetadata().getLastActivityAt())
                .deviceFingerprint(fingerprint != null ? fingerprint.value() : null)
                .deviceName(session.getMetadata().getDeviceName())
                .locationCity(location != null ? location.city() : null)
                .locationCountry(location != null ? location.country() : null)
                .locationCountryCode(location != null ? location.countryCode() : null)
                .locationLatitude(location != null ? location.latitude() : null)
                .locationLongitude(location != null ? location.longitude() : null)
                .isTrustedDevice(session.getMetadata().isTrustedDevice())
                .isActive(isActive)
                .logoutAt(logoutInfo != null ? logoutInfo.getLogoutAt() : null)
                .logoutReason(logoutInfo != null ? this.toLogoutReasonDb(logoutInfo.getLogoutReason()) : null)
                .build();
    }

    @Override
    public @NonNull Session toDomain(@NonNull SessionEntity entity) {
        final Location location = this.buildLocation(entity);
        final DeviceFingerprint fingerprint = entity.getDeviceFingerprint() != null
                ? new DeviceFingerprint(entity.getDeviceFingerprint())
                : null;

        SessionMetadata metadata = SessionMetadata.builder()
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .lastActivityAt(entity.getLastActivityAt())
                .deviceFingerprint(fingerprint)
                .deviceName(entity.getDeviceName())
                .location(location)
                .isTrustedDevice(Boolean.TRUE.equals(entity.getIsTrustedDevice()))
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

    private @Nullable Location buildLocation(@NonNull SessionEntity entity) {
        if (entity.getLocationCity() != null && entity.getLocationCountry() != null
                && entity.getLocationCountryCode() != null) {
            return Location.builder()
                    .city(entity.getLocationCity())
                    .country(entity.getLocationCountry())
                    .countryCode(entity.getLocationCountryCode())
                    .latitude(entity.getLocationLatitude())
                    .longitude(entity.getLocationLongitude())
                    .build();
        }
        return null;
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
            case TIMEOUT, SESSION_EXPIRED -> LogoutReasonDb.TIMEOUT;
            case FORCED -> LogoutReasonDb.FORCED;
            case SECURITY, ADMIN_ACTION, PASSWORD_CHANGED, ACCOUNT_DELETED -> LogoutReasonDb.SECURITY;
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
