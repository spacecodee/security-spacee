package com.spacecodee.securityspacee.session.domain.event;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class SessionExpiredEvent {

    private final SessionId sessionId;
    private final Integer userId;
    private final Instant expiredAt;

    @Override
    public @NonNull String toString() {
        return "SessionExpiredEvent{" +
                "sessionId=" + this.sessionId +
                ", userId=" + this.userId +
                ", expiredAt=" + this.expiredAt +
                '}';
    }
}
