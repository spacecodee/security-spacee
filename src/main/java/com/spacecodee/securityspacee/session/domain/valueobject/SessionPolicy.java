package com.spacecodee.securityspacee.session.domain.valueobject;

import org.jspecify.annotations.NonNull;

import lombok.Builder;

@Builder
public record SessionPolicy(
        @NonNull ConcurrentSessionPolicy type,
        int maxSessions,
        @NonNull SessionExpirationStrategy expirationStrategy,
        boolean notifyOnNewDevice) {

    public static @NonNull SessionPolicy unlimited() {
        return SessionPolicy.builder()
                .type(ConcurrentSessionPolicy.UNLIMITED)
                .maxSessions(Integer.MAX_VALUE)
                .expirationStrategy(SessionExpirationStrategy.EXPLICIT_LOGOUT)
                .notifyOnNewDevice(false)
                .build();
    }

    public static @NonNull SessionPolicy limit5WithFifo() {
        return SessionPolicy.builder()
                .type(ConcurrentSessionPolicy.LIMIT_5)
                .maxSessions(5)
                .expirationStrategy(SessionExpirationStrategy.FIFO)
                .notifyOnNewDevice(true)
                .build();
    }

    public static @NonNull SessionPolicy limit(int maxSessions,
                                               @NonNull SessionExpirationStrategy strategy) {
        return SessionPolicy.builder()
                .type(ConcurrentSessionPolicy.CUSTOM)
                .maxSessions(maxSessions)
                .expirationStrategy(strategy)
                .notifyOnNewDevice(true)
                .build();
    }
}
