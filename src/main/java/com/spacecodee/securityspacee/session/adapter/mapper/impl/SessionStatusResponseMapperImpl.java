package com.spacecodee.securityspacee.session.adapter.mapper.impl;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.adapter.mapper.ISessionStatusResponseMapper;
import com.spacecodee.securityspacee.session.adapter.response.SessionStatusResponse;
import com.spacecodee.securityspacee.session.application.response.SessionExpirationStatus;

public final class SessionStatusResponseMapperImpl implements ISessionStatusResponseMapper {

    @Override
    public @NonNull SessionStatusResponse toResponse(@NonNull SessionExpirationStatus status) {
        return SessionStatusResponse.builder()
                .isExpired(status.isExpired())
                .remainingIdleTimeSeconds(status.getRemainingIdleTime().toSeconds())
                .remainingAbsoluteTimeSeconds(status.getRemainingAbsoluteTime().toSeconds())
                .shouldShowWarning(status.isShouldShowWarning())
                .warningMessage(status.getWarningMessage())
                .build();
    }
}
