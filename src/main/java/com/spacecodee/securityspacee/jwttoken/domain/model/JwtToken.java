package com.spacecodee.securityspacee.jwttoken.domain.model;

import java.time.Instant;
import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.jwttoken.domain.exception.InvalidTokenTypeException;
import com.spacecodee.securityspacee.jwttoken.domain.exception.RevokedTokenException;
import com.spacecodee.securityspacee.jwttoken.domain.exception.TokenAlreadyRevokedException;
import com.spacecodee.securityspacee.jwttoken.domain.exception.TokenExpiredException;
import com.spacecodee.securityspacee.jwttoken.domain.exception.TokenHasNotExpiredException;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Claims;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.RevocationInfo;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public final class JwtToken {

    private final Jti jti;
    private final TokenType tokenType;
    private final String rawToken;
    private final Integer userId;
    private final String sessionId;
    private final TokenState state;
    private final Instant issuedAt;
    private final Instant expiryDate;
    private final Claims claims;
    private final String clientIp;
    private final String userAgent;
    private final RevocationInfo revocationInfo;
    private final Integer refreshCount;
    private final Instant lastRefreshAt;
    private final Integer usageCount;
    private final Instant lastAccessAt;

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }

    public boolean isActive() {
        return this.state == TokenState.ACTIVE && !this.isExpired();
    }

    public boolean isRevoked() {
        return this.state == TokenState.REVOKED;
    }

    public boolean isBlacklisted() {
        return this.state == TokenState.BLACKLISTED;
    }

    @Contract("_, _, _ -> new")
    public @NonNull JwtToken revoke(Integer revokedBy, @NonNull String reason, @NonNull Instant revokedAt) {
        if (this.isRevoked()) {
            throw new TokenAlreadyRevokedException("jwttoken.exception.token_already_revoked");
        }

        RevocationInfo revInfo = RevocationInfo.builder()
                .revokedAt(revokedAt)
                .revokedBy(revokedBy)
                .reason(reason)
                .build();

        return this.toBuilder()
                .state(TokenState.REVOKED)
                .revocationInfo(revInfo)
                .build();
    }

    @Contract("_ -> new")
    public @NonNull JwtToken markAsExpired(@NonNull Instant now) {
        if (now.isBefore(this.expiryDate)) {
            throw new TokenHasNotExpiredException("jwttoken.exception.token_has_not_expired");
        }

        return this.toBuilder()
                .state(TokenState.EXPIRED)
                .build();
    }

    @Contract("_ -> new")
    public @NonNull JwtToken blacklist(@NonNull Instant blacklistedAt) {
        return this.toBuilder()
                .state(TokenState.BLACKLISTED)
                .build();
    }

    @Contract("_ -> new")
    public @NonNull JwtToken incrementUsage(@NonNull Instant accessedAt) {
        return this.toBuilder()
                .usageCount((this.usageCount != null ? this.usageCount : 0) + 1)
                .lastAccessAt(accessedAt)
                .build();
    }

    @Contract("_ -> new")
    public @NonNull JwtToken incrementRefresh(@NonNull Instant refreshedAt) {
        if (this.tokenType != TokenType.REFRESH) {
            throw new InvalidTokenTypeException("jwttoken.exception.invalid_token_type");
        }

        return this.toBuilder()
                .refreshCount((this.refreshCount != null ? this.refreshCount : 0) + 1)
                .lastRefreshAt(refreshedAt)
                .build();
    }

    @Contract("_ -> new")
    public @NonNull JwtToken refresh(@NonNull Instant now) {
        if (this.tokenType != TokenType.REFRESH) {
            throw new InvalidTokenTypeException("jwttoken.exception.only_refresh_tokens_can_refresh");
        }

        if (this.state != TokenState.ACTIVE) {
            throw new RevokedTokenException(
                    "jwttoken.exception.cannot_refresh_token_state_" + this.state.name().toLowerCase());
        }

        if (this.isExpired()) {
            throw new TokenExpiredException("jwttoken.exception.refresh_token_expired", this.expiryDate);
        }

        return this.toBuilder()
                .refreshCount((this.refreshCount != null ? this.refreshCount : 0) + 1)
                .lastRefreshAt(now)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JwtToken jwtToken = (JwtToken) o;
        return Objects.equals(this.jti, jwtToken.jti);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.jti);
    }

    @Override
    public @NonNull String toString() {
        return "JwtToken{" +
                "jti=" + this.jti +
                ", tokenType=" + this.tokenType +
                ", userId=" + this.userId +
                ", sessionId='" + this.sessionId + '\'' +
                ", state=" + this.state +
                ", issuedAt=" + this.issuedAt +
                ", expiryDate=" + this.expiryDate +
                '}';
    }
}
