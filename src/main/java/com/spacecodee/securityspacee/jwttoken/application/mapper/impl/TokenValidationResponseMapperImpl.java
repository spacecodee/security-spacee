package com.spacecodee.securityspacee.jwttoken.application.mapper.impl;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.jwttoken.application.mapper.ITokenValidationResponseMapper;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenValidationResponse;
import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Claims;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;

public final class TokenValidationResponseMapperImpl implements ITokenValidationResponseMapper {

    @Override
    @NonNull
    public TokenValidationResponse toResponse(
            @NonNull Claims claims,
            @NonNull Instant expiry) {

        return new TokenValidationResponse(
                true,
                claims.getJti(),
                Integer.parseInt(claims.getSub()),
                claims.getUsername(),
                claims.getRoles(),
                claims.getSessionId(),
                TokenState.ACTIVE,
                expiry);
    }

    @Override
    @NonNull
    public TokenValidationResponse toResponse(
            @NonNull JwtToken jwtToken,
            @NonNull Claims claims,
            @NonNull Instant expiry) {

        return new TokenValidationResponse(
                true,
                jwtToken.getJti().toString(),
                jwtToken.getUserId(),
                claims.getUsername(),
                claims.getRoles(),
                jwtToken.getSessionId(),
                jwtToken.getState(),
                expiry);
    }
}
