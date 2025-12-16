package com.spacecodee.securityspacee.jwttoken.domain.exception;

import java.time.Instant;

import lombok.Getter;

@Getter
public final class TokenExpiredException extends RuntimeException {

    private final Instant expiredAt;

    public TokenExpiredException(String message, Instant expiredAt) {
        super(message);
        this.expiredAt = expiredAt;
    }
}
