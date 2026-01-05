package com.spacecodee.securityspacee.session.application.mapper;

import java.time.Instant;
import java.util.List;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.response.LogoutAllResponse;

public interface ILogoutAllResponseMapper {

    @NonNull
    LogoutAllResponse toResponse(int sessionsLoggedOut, @NonNull List<String> devices, @NonNull Instant logoutAt,
                                 @NonNull String message);
}
