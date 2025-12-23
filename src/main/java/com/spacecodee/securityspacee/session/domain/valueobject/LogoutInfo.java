package com.spacecodee.securityspacee.session.domain.valueobject;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class LogoutInfo {

    private final Instant logoutAt;
    private final LogoutReason logoutReason;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LogoutInfo that = (LogoutInfo) o;
        return Objects.equals(this.logoutAt, that.logoutAt) &&
                this.logoutReason == that.logoutReason;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.logoutAt, this.logoutReason);
    }

    @Override
    public @NonNull String toString() {
        return "LogoutInfo{" +
                "logoutAt=" + this.logoutAt +
                ", logoutReason=" + this.logoutReason +
                '}';
    }
}
