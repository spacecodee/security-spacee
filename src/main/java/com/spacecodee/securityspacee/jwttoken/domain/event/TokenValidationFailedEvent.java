package com.spacecodee.securityspacee.jwttoken.domain.event;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.jwttoken.domain.valueobject.ValidationFailureReason;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class TokenValidationFailedEvent {

    private final String tokenLastChars;
    private final ValidationFailureReason reason;
    private final Instant attemptedAt;
    private final String ipAddress;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TokenValidationFailedEvent that = (TokenValidationFailedEvent) o;
        return Objects.equals(this.attemptedAt, that.attemptedAt) &&
                Objects.equals(this.ipAddress, that.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.attemptedAt, this.ipAddress);
    }

    @Override
    public @NonNull String toString() {
        return "TokenValidationFailedEvent{" +
                "reason=" + this.reason +
                ", attemptedAt=" + this.attemptedAt +
                ", ipAddress='" + this.ipAddress + '\'' +
                '}';
    }
}
