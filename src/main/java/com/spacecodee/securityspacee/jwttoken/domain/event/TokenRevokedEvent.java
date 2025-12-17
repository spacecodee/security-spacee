package com.spacecodee.securityspacee.jwttoken.domain.event;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class TokenRevokedEvent {

    private final String jti;
    private final TokenType tokenType;
    private final Integer userId;
    private final Instant revokedAt;
    private final Integer revokedBy;
    private final String reason;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TokenRevokedEvent that = (TokenRevokedEvent) o;
        return Objects.equals(this.jti, that.jti) &&
                Objects.equals(this.revokedAt, that.revokedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.jti, this.revokedAt);
    }

    @Override
    public @NonNull String toString() {
        return "TokenRevokedEvent{" +
                "jti='" + this.jti + '\'' +
                ", tokenType=" + this.tokenType +
                ", userId=" + this.userId +
                ", reason='" + this.reason + '\'' +
                '}';
    }
}
