package com.spacecodee.securityspacee.session.domain.valueobject;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.exception.InvalidTimeoutPolicyException;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class TimeoutPolicy {

    private final Duration absoluteTimeout;
    private final Duration idleTimeout;
    private final boolean useSlidingExpiration;
    private final Duration warningThreshold;

    public TimeoutPolicy(
            @NonNull Duration absoluteTimeout,
            @NonNull Duration idleTimeout,
            boolean useSlidingExpiration,
            @NonNull Duration warningThreshold) {
        Objects.requireNonNull(absoluteTimeout, "absoluteTimeout cannot be null");
        Objects.requireNonNull(idleTimeout, "idleTimeout cannot be null");
        Objects.requireNonNull(warningThreshold, "warningThreshold cannot be null");

        if (idleTimeout.compareTo(absoluteTimeout) >= 0) {
            throw new InvalidTimeoutPolicyException("session.error.idle_timeout_must_be_less_than_absolute");
        }

        if (warningThreshold.compareTo(absoluteTimeout) >= 0) {
            throw new InvalidTimeoutPolicyException("session.error.warning_threshold_must_be_less_than_absolute");
        }

        this.absoluteTimeout = absoluteTimeout;
        this.idleTimeout = idleTimeout;
        this.useSlidingExpiration = useSlidingExpiration;
        this.warningThreshold = warningThreshold;
    }

    public @NonNull Instant calculateAbsoluteExpiration(@NonNull Instant createdAt) {
        Objects.requireNonNull(createdAt, "createdAt cannot be null");
        return createdAt.plus(this.absoluteTimeout);
    }

    public boolean shouldWarn(@NonNull Duration remainingTime) {
        Objects.requireNonNull(remainingTime, "remainingTime cannot be null");
        return remainingTime.compareTo(this.warningThreshold) <= 0 && !remainingTime.isNegative();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TimeoutPolicy that = (TimeoutPolicy) o;
        return this.useSlidingExpiration == that.useSlidingExpiration &&
                Objects.equals(this.absoluteTimeout, that.absoluteTimeout) &&
                Objects.equals(this.idleTimeout, that.idleTimeout) &&
                Objects.equals(this.warningThreshold, that.warningThreshold);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.absoluteTimeout, this.idleTimeout, this.useSlidingExpiration, this.warningThreshold);
    }

    @Override
    public @NonNull String toString() {
        return "TimeoutPolicy{" +
                "absoluteTimeout=" + this.absoluteTimeout +
                ", idleTimeout=" + this.idleTimeout +
                ", useSlidingExpiration=" + this.useSlidingExpiration +
                ", warningThreshold=" + this.warningThreshold +
                '}';
    }
}
