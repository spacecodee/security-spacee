package com.spacecodee.securityspacee.session.application.response;

import java.time.Duration;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class SessionExpirationStatus {

    private final boolean isExpired;
    private final Duration remainingIdleTime;
    private final Duration remainingAbsoluteTime;
    private final boolean shouldShowWarning;
    @Nullable
    private final String warningMessage;

    public SessionExpirationStatus(
            boolean isExpired,
            @NonNull Duration remainingIdleTime,
            @NonNull Duration remainingAbsoluteTime,
            boolean shouldShowWarning,
            @Nullable String warningMessage) {
        this.isExpired = isExpired;
        this.remainingIdleTime = Objects.requireNonNull(remainingIdleTime, "remainingIdleTime cannot be null");
        this.remainingAbsoluteTime = Objects.requireNonNull(remainingAbsoluteTime,
                "remainingAbsoluteTime cannot be null");
        this.shouldShowWarning = shouldShowWarning;
        this.warningMessage = warningMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SessionExpirationStatus that = (SessionExpirationStatus) o;
        return this.isExpired == that.isExpired &&
                this.shouldShowWarning == that.shouldShowWarning &&
                Objects.equals(this.remainingIdleTime, that.remainingIdleTime) &&
                Objects.equals(this.remainingAbsoluteTime, that.remainingAbsoluteTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.isExpired, this.remainingIdleTime, this.remainingAbsoluteTime,
                this.shouldShowWarning);
    }

    @Override
    public @NonNull String toString() {
        return "SessionExpirationStatus{" +
                "isExpired=" + this.isExpired +
                ", remainingIdleTime=" + this.remainingIdleTime +
                ", remainingAbsoluteTime=" + this.remainingAbsoluteTime +
                ", shouldShowWarning=" + this.shouldShowWarning +
                ", warningMessage='" + this.warningMessage + '\'' +
                '}';
    }
}
