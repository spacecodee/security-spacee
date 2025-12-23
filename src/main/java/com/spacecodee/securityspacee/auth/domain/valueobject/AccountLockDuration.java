package com.spacecodee.securityspacee.auth.domain.valueobject;

import java.time.Duration;
import java.time.Instant;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Getter;

@Getter
public final class AccountLockDuration {

    private final Instant lockedUntil;

    private AccountLockDuration(Instant lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    @Contract("_ -> new")
    public static @NonNull AccountLockDuration standard(long durationInMinutes) {
        return new AccountLockDuration(Instant.now().plus(Duration.ofMinutes(durationInMinutes)));
    }

    @Contract("_ -> new")
    public static @NonNull AccountLockDuration custom(Duration duration) {
        return new AccountLockDuration(Instant.now().plus(duration));
    }

    public boolean isExpired() {
        return Instant.now().isAfter(lockedUntil);
    }
}
