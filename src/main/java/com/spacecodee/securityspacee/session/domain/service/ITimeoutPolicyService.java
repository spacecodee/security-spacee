package com.spacecodee.securityspacee.session.domain.service;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.valueobject.TimeoutPolicy;

public interface ITimeoutPolicyService {

    @NonNull
    TimeoutPolicy getPolicyForUser(@NonNull Integer userId);

    @NonNull
    TimeoutPolicy getDefaultPolicy();
}
