package com.spacecodee.securityspacee.session.domain.valueobject;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public final class LogoutInfo {

    private final Instant logoutAt;
    private final LogoutReason logoutReason;
    @Nullable
    private final Integer loggedOutBy;

    public boolean wasLoggedOutByOwner(@NonNull Integer owner) {
        Objects.requireNonNull(owner, "owner cannot be null");
        return Objects.equals(this.loggedOutBy, owner);
    }

    public boolean wasForcedLogout() {
        return this.logoutReason == LogoutReason.FORCED
                || this.logoutReason == LogoutReason.ADMIN_ACTION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LogoutInfo that = (LogoutInfo) o;
        return Objects.equals(this.logoutAt, that.logoutAt) &&
                this.logoutReason == that.logoutReason &&
                Objects.equals(this.loggedOutBy, that.loggedOutBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.logoutAt, this.logoutReason, this.loggedOutBy);
    }

    @Override
    public @NonNull String toString() {
        return "LogoutInfo{" +
                "logoutAt=" + this.logoutAt +
                ", logoutReason=" + this.logoutReason +
                ", loggedOutBy=" + this.loggedOutBy +
                '}';
    }
}
