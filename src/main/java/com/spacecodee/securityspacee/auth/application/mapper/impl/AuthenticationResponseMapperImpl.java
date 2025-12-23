package com.spacecodee.securityspacee.auth.application.mapper.impl;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.auth.application.mapper.IAuthenticationResponseMapper;
import com.spacecodee.securityspacee.auth.application.port.out.TokenPair;
import com.spacecodee.securityspacee.auth.application.response.AuthenticationResponse;
import com.spacecodee.securityspacee.auth.application.response.UserInfo;
import com.spacecodee.securityspacee.auth.domain.valueobject.AuthenticationResult;

public final class AuthenticationResponseMapperImpl implements IAuthenticationResponseMapper {

    @Override
    public @NonNull AuthenticationResponse toResponse(@NonNull AuthenticationResult authResult,
                                                      @NonNull TokenPair tokenPair) {
        UserInfo userInfo = new UserInfo(
                authResult.getUserId(),
                authResult.getUsername(),
                authResult.getEmail(),
                authResult.getUserType(),
                authResult.getAssignedRoles());

        return new AuthenticationResponse(
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                tokenPair.expiresIn(),
                "Bearer",
                userInfo);
    }
}
