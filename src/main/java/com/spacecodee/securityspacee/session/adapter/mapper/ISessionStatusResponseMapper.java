package com.spacecodee.securityspacee.session.adapter.mapper;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.adapter.response.SessionStatusResponse;
import com.spacecodee.securityspacee.session.application.response.SessionExpirationStatus;

public interface ISessionStatusResponseMapper {

    @NonNull
    SessionStatusResponse toResponse(@NonNull SessionExpirationStatus status);
}
