package com.spacecodee.securityspacee.jwttoken.domain.exception;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.AuthenticationException;

import lombok.Getter;

@Getter
public final class TokenRevokedException extends AuthenticationException {

    private final String jti;
    private final Instant revokedAt;
    private final String reason;

    public TokenRevokedException(@NonNull String message, @NonNull String jti, @NonNull Instant revokedAt,
                                 @NonNull String reason) {
        super(message);
        this.jti = jti;
        this.revokedAt = revokedAt;
        this.reason = reason;
    }

}
