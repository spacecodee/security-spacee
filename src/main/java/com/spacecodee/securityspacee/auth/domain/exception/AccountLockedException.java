package com.spacecodee.securityspacee.auth.domain.exception;

import lombok.Getter;

import java.time.Instant;

@Getter
public final class AccountLockedException extends RuntimeException {

    private final Instant lockedUntil;

    public AccountLockedException(String message, Instant lockedUntil) {
        super(message);
        this.lockedUntil = lockedUntil;
    }

}
