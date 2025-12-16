package com.spacecodee.securityspacee.auth.domain.event;

import java.time.Instant;
import java.util.Objects;

import lombok.Getter;

@Getter
public final class AccountLockedEvent {

    private final Integer userId;
    private final String username;
    private final String email;
    private final Instant lockedUntil;
    private final String reason;
    private final Instant timestamp;

    public AccountLockedEvent(
            Integer userId,
            String username,
            String email,
            Instant lockedUntil,
            String reason,
            Instant timestamp) {
        this.userId = Objects.requireNonNull(userId);
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
        this.lockedUntil = Objects.requireNonNull(lockedUntil);
        this.reason = Objects.requireNonNull(reason);
        this.timestamp = Objects.requireNonNull(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AccountLockedEvent that = (AccountLockedEvent) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, timestamp);
    }

    @Override
    public String toString() {
        return "AccountLockedEvent{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", lockedUntil=" + lockedUntil +
                ", reason='" + reason + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
