package com.spacecodee.securityspacee.jwttoken.application.event;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class SessionLoggedOutEvent {

    private final String sessionId;
    private final Integer userId;
    private final Instant logoutAt;
    private final String logoutReason;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        SessionLoggedOutEvent that = (SessionLoggedOutEvent) o;
        return Objects.equals(this.sessionId, that.sessionId) &&
                Objects.equals(this.logoutAt, that.logoutAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sessionId, this.logoutAt);
    }

    @Override
    public @NonNull String toString() {
        return "SessionLoggedOutEvent{" +
                "sessionId='" + this.sessionId + '\'' +
                ", userId=" + this.userId +
                ", logoutReason='" + this.logoutReason + '\'' +
                '}';
    }
}
