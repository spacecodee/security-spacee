package com.spacecodee.securityspacee.jwttoken.application.mapper;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.jwttoken.application.response.TokenValidationResponse;
import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Claims;

public interface ITokenValidationResponseMapper {

    @NonNull
    TokenValidationResponse toResponse(
            @NonNull Claims claims,
            @NonNull Instant expiry);

    @NonNull
    TokenValidationResponse toResponse(
            @NonNull JwtToken jwtToken,
            @NonNull Claims claims,
            @NonNull Instant expiry);
}
