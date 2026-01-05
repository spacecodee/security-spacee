package com.spacecodee.securityspacee.jwttoken.domain.event;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class AllSessionTokensRevokedEvent {

    private final String sessionId;
    private final Integer userId;
    private final Integer tokensRevokedCount;
    private final Instant revokedAt;
    private final Integer revokedBy;
    private final String reason;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        AllSessionTokensRevokedEvent that = (AllSessionTokensRevokedEvent) o;
        return Objects.equals(this.sessionId, that.sessionId) &&
                Objects.equals(this.revokedAt, that.revokedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sessionId, this.revokedAt);
    }

    @Override
    public @NonNull String toString() {
        return "AllSessionTokensRevokedEvent{" +
                "sessionId='" + this.sessionId + '\'' +
                ", userId=" + this.userId +
                ", tokensRevokedCount=" + this.tokensRevokedCount +
                ", reason='" + this.reason + '\'' +
                '}';
    }
}
