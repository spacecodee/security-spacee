package com.spacecodee.securityspacee.session.domain.service;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.valueobject.SessionPolicy;

public interface ISessionPolicyService {

    @NonNull
    SessionPolicy getPolicyForUser(@NonNull Integer userId);

    @NonNull
    SessionPolicy getDefaultPolicy();
}
