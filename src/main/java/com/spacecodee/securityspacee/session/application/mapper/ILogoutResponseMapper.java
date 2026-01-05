package com.spacecodee.securityspacee.session.application.mapper;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.response.LogoutResponse;
import com.spacecodee.securityspacee.session.domain.model.Session;

public interface ILogoutResponseMapper {

    @NonNull
    LogoutResponse toResponse(@NonNull Session session);
}
