package com.spacecodee.securityspacee.session.infrastructure.service;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.service.ITimeoutPolicyService;
import com.spacecodee.securityspacee.session.domain.valueobject.TimeoutPolicy;
import com.spacecodee.securityspacee.session.infrastructure.config.properties.SessionTimeoutProperties;

public final class DefaultTimeoutPolicyService implements ITimeoutPolicyService {

    private final SessionTimeoutProperties properties;

    public DefaultTimeoutPolicyService(@NonNull SessionTimeoutProperties properties) {
        this.properties = properties;
    }

    @Override
    public @NonNull TimeoutPolicy getPolicyForUser(@NonNull Integer userId) {
        return this.getDefaultPolicy();
    }

    @Override
    public @NonNull TimeoutPolicy getDefaultPolicy() {
        return TimeoutPolicy.builder()
                .absoluteTimeout(this.properties.getAbsolute())
                .idleTimeout(this.properties.getIdle())
                .useSlidingExpiration(this.properties.isSlidingExpiration())
                .warningThreshold(this.properties.getWarningThreshold())
                .build();
    }
}
