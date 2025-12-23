package com.spacecodee.securityspacee.session.domain.event;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class SessionExpiredByAbsoluteTimeoutEvent {

    private final String sessionId;
    private final Integer userId;
    private final Instant createdAt;
    private final Instant expiredAt;
    private final Duration sessionDuration;
    private final String reason;

    public SessionExpiredByAbsoluteTimeoutEvent(
            @NonNull String sessionId,
            @NonNull Integer userId,
            @NonNull Instant createdAt,
            @NonNull Instant expiredAt,
            @NonNull Duration sessionDuration,
            @NonNull String reason) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId cannot be null");
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt cannot be null");
        this.expiredAt = Objects.requireNonNull(expiredAt, "expiredAt cannot be null");
        this.sessionDuration = Objects.requireNonNull(sessionDuration, "sessionDuration cannot be null");
        this.reason = Objects.requireNonNull(reason, "reason cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SessionExpiredByAbsoluteTimeoutEvent that = (SessionExpiredByAbsoluteTimeoutEvent) o;
        return Objects.equals(this.sessionId, that.sessionId) &&
                Objects.equals(this.userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sessionId, this.userId);
    }

    @Override
    public @NonNull String toString() {
        return "SessionExpiredByAbsoluteTimeoutEvent{" +
                "sessionId='" + this.sessionId + '\'' +
                ", userId=" + this.userId +
                ", createdAt=" + this.createdAt +
                ", expiredAt=" + this.expiredAt +
                ", sessionDuration=" + this.sessionDuration +
                ", reason='" + this.reason + '\'' +
                '}';
    }
}
