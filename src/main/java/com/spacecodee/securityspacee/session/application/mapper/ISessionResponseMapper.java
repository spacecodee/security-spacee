package com.spacecodee.securityspacee.session.application.mapper;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.response.SessionResponse;
import com.spacecodee.securityspacee.session.domain.model.Session;

public interface ISessionResponseMapper {

    @NonNull
    SessionResponse toResponse(@NonNull Session session);
}
