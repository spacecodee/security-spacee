package com.spacecodee.securityspacee.jwttoken.domain.exception;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.AuthenticationException;

import lombok.Getter;

@Getter
public final class TokenExpiredException extends AuthenticationException {

    private final Instant expiredAt;

    public TokenExpiredException(@NonNull String message, @NonNull Instant expiredAt) {
        super(message);
        this.expiredAt = expiredAt;
    }

}
