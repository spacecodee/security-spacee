package com.spacecodee.securityspacee.jwttoken.domain.event;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class TokenRefreshedEvent {

    private final String oldAccessTokenJti;
    private final String newAccessTokenJti;
    private final String refreshTokenJti;
    private final Integer userId;
    private final String sessionId;
    private final Instant refreshedAt;
    private final Integer refreshCount;
    private final Instant newAccessTokenExpiresAt;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TokenRefreshedEvent that = (TokenRefreshedEvent) o;
        return Objects.equals(this.newAccessTokenJti, that.newAccessTokenJti) &&
                Objects.equals(this.refreshedAt, that.refreshedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.newAccessTokenJti, this.refreshedAt);
    }

    @Override
    public @NonNull String toString() {
        return "TokenRefreshedEvent{" +
                "oldAccessTokenJti='" + this.oldAccessTokenJti + '\'' +
                ", newAccessTokenJti='" + this.newAccessTokenJti + '\'' +
                ", userId=" + this.userId +
                ", sessionId='" + this.sessionId + '\'' +
                ", refreshCount=" + this.refreshCount +
                ", newAccessTokenExpiresAt=" + this.newAccessTokenExpiresAt +
                '}';
    }
}
