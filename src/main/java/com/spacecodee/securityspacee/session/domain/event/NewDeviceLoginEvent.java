package com.spacecodee.securityspacee.session.domain.event;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.spacecodee.securityspacee.session.domain.valueobject.Location;

import lombok.Builder;

@Builder
public record NewDeviceLoginEvent(
        @NonNull Integer userId,
        @NonNull String sessionId,
        @Nullable String deviceName,
        @Nullable Location location,
        @NonNull String ipAddress,
        boolean isTrustedDevice,
        @NonNull Instant loginAt) {
}
