package com.spacecodee.securityspacee.session.infrastructure.service;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;

import com.spacecodee.securityspacee.session.domain.service.ISessionPolicyService;
import com.spacecodee.securityspacee.session.domain.valueobject.ConcurrentSessionPolicy;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionExpirationStrategy;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionPolicy;

public final class DefaultSessionPolicyService implements ISessionPolicyService {

    private final int defaultMaxSessions;
    private final SessionExpirationStrategy defaultStrategy;
    private final boolean notifyOnNewDevice;

    public DefaultSessionPolicyService(
            @Value("${session.concurrent.default-max}") int defaultMaxSessions,
            @Value("${session.concurrent.default-strategy}") String defaultStrategyStr,
            @Value("${session.concurrent.notify-new-device}") boolean notifyOnNewDevice) {
        this.defaultMaxSessions = defaultMaxSessions;
        this.defaultStrategy = SessionExpirationStrategy.valueOf(defaultStrategyStr);
        this.notifyOnNewDevice = notifyOnNewDevice;
    }

    @Override
    public @NonNull SessionPolicy getPolicyForUser(@NonNull Integer userId) {
        return this.getDefaultPolicy();
    }

    @Override
    public @NonNull SessionPolicy getDefaultPolicy() {
        return SessionPolicy.builder()
                .type(this.determinePolicyType(this.defaultMaxSessions))
                .maxSessions(this.defaultMaxSessions)
                .expirationStrategy(this.defaultStrategy)
                .notifyOnNewDevice(this.notifyOnNewDevice)
                .build();
    }

    private @NonNull ConcurrentSessionPolicy determinePolicyType(int maxSessions) {
        return switch (maxSessions) {
            case 1 -> ConcurrentSessionPolicy.LIMIT_1;
            case 3 -> ConcurrentSessionPolicy.LIMIT_3;
            case 5 -> ConcurrentSessionPolicy.LIMIT_5;
            case Integer.MAX_VALUE -> ConcurrentSessionPolicy.UNLIMITED;
            default -> ConcurrentSessionPolicy.CUSTOM;
        };
    }
}
