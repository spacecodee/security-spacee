package com.spacecodee.securityspacee.jwttoken.domain.event;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class TokensIssuedEvent {

    private final String accessTokenJti;
    private final String refreshTokenJti;
    private final Integer userId;
    private final String sessionId;
    private final Instant issuedAt;
    private final Instant accessTokenExpiresAt;
    private final Instant refreshTokenExpiresAt;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TokensIssuedEvent that = (TokensIssuedEvent) o;
        return Objects.equals(this.accessTokenJti, that.accessTokenJti) &&
                Objects.equals(this.refreshTokenJti, that.refreshTokenJti);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.accessTokenJti, this.refreshTokenJti);
    }

    @Override
    public @NonNull String toString() {
        return "TokensIssuedEvent{" +
                "userId=" + this.userId +
                ", sessionId='" + this.sessionId + '\'' +
                ", issuedAt=" + this.issuedAt +
                '}';
    }
}
