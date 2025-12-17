package com.spacecodee.securityspacee.jwttoken.application.mapper;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.jwttoken.application.command.IssueTokenCommand;
import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Claims;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;
import com.spacecodee.securityspacee.shared.config.properties.JwtProperties;

public interface IClaimsMapper {

    @NonNull
    Claims buildAccessClaims(
            @NonNull Jti jti,
            @NonNull IssueTokenCommand command,
            @NonNull Instant now,
            @NonNull Instant expiry,
            @NonNull JwtProperties jwtProperties);

    @NonNull
    Claims buildRefreshClaims(
            @NonNull Jti jti,
            @NonNull IssueTokenCommand command,
            @NonNull Instant now,
            @NonNull Instant expiry,
            @NonNull JwtProperties jwtProperties);

    @NonNull
    Claims buildAccessClaimsFromRefresh(
            @NonNull Jti jti,
            @NonNull JwtToken refreshToken,
            @NonNull Instant now,
            @NonNull Instant expiry,
            @NonNull JwtProperties jwtProperties);
}
