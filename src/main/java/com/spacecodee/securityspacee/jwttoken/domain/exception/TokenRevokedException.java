package com.spacecodee.securityspacee.jwttoken.domain.exception;

import java.time.Instant;

import lombok.Getter;

@Getter
public final class TokenRevokedException extends RuntimeException {

    private final String jti;
    private final Instant revokedAt;
    private final String reason;

    public TokenRevokedException(String message, String jti, Instant revokedAt, String reason) {
        super(message);
        this.jti = jti;
        this.revokedAt = revokedAt;
        this.reason = reason;
    }
}
