package com.spacecodee.securityspacee.jwttoken.domain.valueobject;

import java.util.Objects;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class TokenPair {

    private final String accessToken;
    private final String refreshToken;
    private final long expiresIn;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TokenPair tokenPair = (TokenPair) o;
        return Objects.equals(this.accessToken, tokenPair.accessToken) &&
                Objects.equals(this.refreshToken, tokenPair.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.accessToken, this.refreshToken);
    }

    @Override
    public @NonNull String toString() {
        return "TokenPair{" +
                "accessToken='" + this.accessToken.substring(0, Math.min(20, this.accessToken.length())) + "...'" +
                ", refreshToken='" + this.refreshToken.substring(0, Math.min(20, this.refreshToken.length())) + "...'" +
                ", expiresIn=" + this.expiresIn +
                '}';
    }
}
