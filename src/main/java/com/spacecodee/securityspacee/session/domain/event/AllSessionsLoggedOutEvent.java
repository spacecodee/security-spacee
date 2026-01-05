package com.spacecodee.securityspacee.session.domain.event;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class AllSessionsLoggedOutEvent {

    private final Integer userId;
    private final Integer sessionCount;
    private final Instant loggedOutAt;
    private final String reason;
    private final Integer loggedOutBy;

    @Override
    public @NonNull String toString() {
        return "AllSessionsLoggedOutEvent{" +
                "userId=" + this.userId +
                ", sessionCount=" + this.sessionCount +
                ", loggedOutAt=" + this.loggedOutAt +
                ", reason='" + this.reason + '\'' +
                ", loggedOutBy=" + this.loggedOutBy +
                '}';
    }
}
