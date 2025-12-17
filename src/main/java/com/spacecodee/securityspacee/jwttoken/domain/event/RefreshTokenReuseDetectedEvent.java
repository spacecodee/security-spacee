package com.spacecodee.securityspacee.jwttoken.domain.event;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class RefreshTokenReuseDetectedEvent {

    private final String refreshTokenJti;
    private final Integer userId;
    private final Instant attemptedAt;
    private final String ipAddress;
    private final String sessionId;
    private final String reason;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RefreshTokenReuseDetectedEvent that = (RefreshTokenReuseDetectedEvent) o;
        return Objects.equals(this.refreshTokenJti, that.refreshTokenJti) &&
                Objects.equals(this.attemptedAt, that.attemptedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.refreshTokenJti, this.attemptedAt);
    }

    @Override
    public @NonNull String toString() {
        return "RefreshTokenReuseDetectedEvent{" +
                "refreshTokenJti='" + this.refreshTokenJti + '\'' +
                ", userId=" + this.userId +
                ", sessionId='" + this.sessionId + '\'' +
                ", ipAddress='" + this.ipAddress + '\'' +
                ", reason='" + this.reason + '\'' +
                ", attemptedAt=" + this.attemptedAt +
                '}';
    }
}
