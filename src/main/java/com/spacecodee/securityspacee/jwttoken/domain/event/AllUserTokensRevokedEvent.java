package com.spacecodee.securityspacee.jwttoken.domain.event;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class AllUserTokensRevokedEvent {

    private final Integer userId;
    private final Integer tokensRevokedCount;
    private final Integer sessionsAffectedCount;
    private final Instant revokedAt;
    private final Integer revokedBy;
    private final String reason;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        AllUserTokensRevokedEvent that = (AllUserTokensRevokedEvent) o;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.revokedAt, that.revokedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.revokedAt);
    }

    @Override
    public @NonNull String toString() {
        return "AllUserTokensRevokedEvent{" +
                "userId=" + this.userId +
                ", tokensRevokedCount=" + this.tokensRevokedCount +
                ", sessionsAffectedCount=" + this.sessionsAffectedCount +
                ", reason='" + this.reason + '\'' +
                '}';
    }
}
