package com.spacecodee.securityspacee.jwttoken.application.event;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class UserPasswordChangedEvent {

    private final Integer userId;
    private final Instant changedAt;
    private final Integer changedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        UserPasswordChangedEvent that = (UserPasswordChangedEvent) o;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.changedAt, that.changedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.userId, this.changedAt);
    }

    @Override
    public @NonNull String toString() {
        return "UserPasswordChangedEvent{" +
                "userId=" + this.userId +
                ", changedAt=" + this.changedAt +
                '}';
    }
}
