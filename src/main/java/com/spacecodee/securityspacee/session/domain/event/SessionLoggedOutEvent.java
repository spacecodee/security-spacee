package com.spacecodee.securityspacee.session.domain.event;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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
    @Nullable
    private final Integer loggedOutBy;
    @Nullable
    private final String deviceName;
    private final boolean wasRemoteLogout;

    @Override
    public @NonNull String toString() {
        return "SessionLoggedOutEvent{" +
                "sessionId=" + this.sessionId +
                ", userId=" + this.userId +
                ", logoutReason=" + this.logoutReason +
                ", loggedOutAt=" + this.loggedOutAt +
                ", loggedOutBy=" + this.loggedOutBy +
                ", deviceName='" + this.deviceName + '\'' +
                ", wasRemoteLogout=" + this.wasRemoteLogout +
                '}';
    }
}
