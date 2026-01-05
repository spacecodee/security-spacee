package com.spacecodee.securityspacee.jwttoken.application.mapper.impl;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.jwttoken.application.command.IssueTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.mapper.IClaimsMapper;
import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Claims;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenType;
import com.spacecodee.securityspacee.shared.config.properties.JwtProperties;

public final class ClaimsMapperImpl implements IClaimsMapper {

    @Override
    @NonNull
    public Claims buildAccessClaims(
            @NonNull Jti jti,
            @NonNull IssueTokenCommand command,
            @NonNull Instant now,
            @NonNull Instant expiry,
            @NonNull JwtProperties jwtProperties) {

        return Claims.builder()
                .jti(jti.getValue().toString())
                .sub(command.userId().toString())
                .username(command.username())
                .roles(command.roles())
                .sessionId(command.sessionId())
                .iat(now)
                .exp(expiry)
                .iss(jwtProperties.issuer())
                .aud(jwtProperties.audience())
                .build();
    }

    @Override
    @NonNull
    public Claims buildRefreshClaims(
            @NonNull Jti jti,
            @NonNull IssueTokenCommand command,
            @NonNull Instant now,
            @NonNull Instant expiry,
            @NonNull JwtProperties jwtProperties) {

        return Claims.builder()
                .jti(jti.getValue().toString())
                .sub(command.userId().toString())
                .sessionId(command.sessionId())
                .tokenType(TokenType.REFRESH.name())
                .iat(now)
                .exp(expiry)
                .iss(jwtProperties.issuer())
                .aud(jwtProperties.audience())
                .build();
    }

    @Override
    @NonNull
    public Claims buildAccessClaimsFromRefresh(
            @NonNull Jti jti,
            @NonNull JwtToken refreshToken,
            @NonNull Instant now,
            @NonNull Instant expiry,
            @NonNull JwtProperties jwtProperties) {

        return Claims.builder()
                .jti(jti.getValue().toString())
                .sub(refreshToken.getUserId().toString())
                .username(refreshToken.getClaims().getUsername())
                .roles(refreshToken.getClaims().getRoles())
                .sessionId(refreshToken.getSessionId())
                .iat(now)
                .exp(expiry)
                .iss(jwtProperties.issuer())
                .aud(jwtProperties.audience())
                .build();
    }
}
