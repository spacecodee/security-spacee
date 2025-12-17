package com.spacecodee.securityspacee.session.domain.event;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.valueobject.LogoutReason;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class SessionLoggedOutEvent {

    private final SessionId sessionId;
    private final Integer userId;
    private final LogoutReason logoutReason;
    private final Instant loggedOutAt;

    @Override
    public @NonNull String toString() {
        return "SessionLoggedOutEvent{" +
                "sessionId=" + this.sessionId +
                ", userId=" + this.userId +
                ", logoutReason=" + this.logoutReason +
                ", loggedOutAt=" + this.loggedOutAt +
                '}';
    }
}
