package com.spacecodee.securityspacee.session.domain.event;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class SessionExpiredByIdleEvent {

    private final String sessionId;
    private final Integer userId;
    private final Instant lastActivityAt;
    private final Instant expiredAt;
    private final Duration idleTime;
    private final String reason;

    public SessionExpiredByIdleEvent(
            @NonNull String sessionId,
            @NonNull Integer userId,
            @NonNull Instant lastActivityAt,
            @NonNull Instant expiredAt,
            @NonNull Duration idleTime,
            @NonNull String reason) {
        this.sessionId = Objects.requireNonNull(sessionId, "sessionId cannot be null");
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");
        this.lastActivityAt = Objects.requireNonNull(lastActivityAt, "lastActivityAt cannot be null");
        this.expiredAt = Objects.requireNonNull(expiredAt, "expiredAt cannot be null");
        this.idleTime = Objects.requireNonNull(idleTime, "idleTime cannot be null");
        this.reason = Objects.requireNonNull(reason, "reason cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SessionExpiredByIdleEvent that = (SessionExpiredByIdleEvent) o;
        return Objects.equals(this.sessionId, that.sessionId) &&
                Objects.equals(this.userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sessionId, this.userId);
    }

    @Override
    public @NonNull String toString() {
        return "SessionExpiredByIdleEvent{" +
                "sessionId='" + this.sessionId + '\'' +
                ", userId=" + this.userId +
                ", lastActivityAt=" + this.lastActivityAt +
                ", expiredAt=" + this.expiredAt +
                ", idleTime=" + this.idleTime +
                ", reason='" + this.reason + '\'' +
                '}';
    }
}
