package com.spacecodee.securityspacee.jwttoken.application.response;

import java.time.Instant;
import java.util.List;

import org.jspecify.annotations.Nullable;

import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenMetadata;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;

public record TokenValidationResponse(
        boolean valid,
        String jti,
        Integer userId,
        String username,
        List<String> roles,
        String sessionId,
        TokenState state,
        Instant expiresAt,
        @Nullable TokenMetadata metadata) {
}
