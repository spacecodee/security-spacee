package com.spacecodee.securityspacee.auth.domain.exception;

import java.time.Instant;

import com.spacecodee.securityspacee.shared.exception.base.AccountLockedException;
import org.jspecify.annotations.NonNull;

public final class UserAccountLockedException extends AccountLockedException {

    public UserAccountLockedException(@NonNull String message, @NonNull Instant lockedUntil) {
        super(message, lockedUntil);
    }

}
