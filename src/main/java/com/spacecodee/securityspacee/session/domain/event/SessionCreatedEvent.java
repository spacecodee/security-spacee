package com.spacecodee.securityspacee.session.domain.event;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionToken;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class SessionCreatedEvent {

    private final SessionId sessionId;
    private final SessionToken sessionToken;
    private final Integer userId;
    private final Instant createdAt;
    private final Instant expiresAt;

    @Override
    public @NonNull String toString() {
        return "SessionCreatedEvent{" +
                "sessionId=" + this.sessionId +
                ", userId=" + this.userId +
                ", createdAt=" + this.createdAt +
                '}';
    }
}
