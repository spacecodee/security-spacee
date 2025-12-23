package com.spacecodee.securityspacee.session.domain.event;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class SessionActivityUpdatedEvent {

    private final SessionId sessionId;
    private final Integer userId;
    private final Instant lastActivityAt;

    @Override
    public @NonNull String toString() {
        return "SessionActivityUpdatedEvent{" +
                "sessionId=" + this.sessionId +
                ", userId=" + this.userId +
                ", lastActivityAt=" + this.lastActivityAt +
                '}';
    }
}
