package com.spacecodee.securityspacee.jwttoken.application.event;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class SessionExpiredEvent {

    private final String sessionId;
    private final Integer userId;
    private final Instant expiredAt;
    private final String expirationReason;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        SessionExpiredEvent that = (SessionExpiredEvent) o;
        return Objects.equals(this.sessionId, that.sessionId) &&
                Objects.equals(this.expiredAt, that.expiredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sessionId, this.expiredAt);
    }

    @Override
    public @NonNull String toString() {
        return "SessionExpiredEvent{" +
                "sessionId='" + this.sessionId + '\'' +
                ", userId=" + this.userId +
                ", expirationReason='" + this.expirationReason + '\'' +
                '}';
    }
}
