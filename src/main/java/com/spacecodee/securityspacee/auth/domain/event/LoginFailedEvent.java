package com.spacecodee.securityspacee.auth.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.spacecodee.securityspacee.auth.domain.valueobject.FailureReason;

import lombok.Getter;

@Getter
public final class LoginFailedEvent {

    private final String usernameOrEmail;
    private final FailureReason reason;
    private final Integer failedAttempts;
    private final String ipAddress;
    private final Instant timestamp;

    public LoginFailedEvent(
            String usernameOrEmail,
            FailureReason reason,
            Integer failedAttempts,
            String ipAddress,
            Instant timestamp) {
        this.usernameOrEmail = Objects.requireNonNull(usernameOrEmail);
        this.reason = Objects.requireNonNull(reason);
        this.failedAttempts = failedAttempts;
        this.ipAddress = ipAddress;
        this.timestamp = Objects.requireNonNull(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LoginFailedEvent that = (LoginFailedEvent) o;
        return Objects.equals(usernameOrEmail, that.usernameOrEmail) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usernameOrEmail, timestamp);
    }

    @Override
    public String toString() {
        return "LoginFailedEvent{" +
                "usernameOrEmail='" + usernameOrEmail + '\'' +
                ", reason=" + reason +
                ", failedAttempts=" + failedAttempts +
                ", timestamp=" + timestamp +
                '}';
    }
}
