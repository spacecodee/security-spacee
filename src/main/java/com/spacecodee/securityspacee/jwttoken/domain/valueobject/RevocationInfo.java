package com.spacecodee.securityspacee.jwttoken.domain.valueobject;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class RevocationInfo {

    private final Instant revokedAt;
    private final Integer revokedBy;
    private final String reason;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RevocationInfo that = (RevocationInfo) o;
        return Objects.equals(this.revokedAt, that.revokedAt) &&
                Objects.equals(this.revokedBy, that.revokedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.revokedAt, this.revokedBy);
    }

    @Override
    public @NonNull String toString() {
        return "RevocationInfo{" +
                "revokedAt=" + this.revokedAt +
                ", revokedBy=" + this.revokedBy +
                ", reason='" + this.reason + '\'' +
                '}';
    }
}
