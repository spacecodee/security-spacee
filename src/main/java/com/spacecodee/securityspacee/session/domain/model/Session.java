package com.spacecodee.securityspacee.session.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.spacecodee.securityspacee.session.domain.exception.SessionInvalidStateException;
import com.spacecodee.securityspacee.session.domain.valueobject.LogoutInfo;
import com.spacecodee.securityspacee.session.domain.valueobject.LogoutReason;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionMetadata;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionState;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionToken;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public final class Session {

    private final SessionId sessionId;
    private final SessionToken sessionToken;
    private final Integer userId;
    private final SessionMetadata metadata;
    private final SessionState state;
    @Nullable
    private final LogoutInfo logoutInfo;

    public @NonNull Session expire() {
        this.validateStateTransition(SessionState.EXPIRED);
        return this.toBuilder()
                .state(SessionState.EXPIRED)
                .build();
    }

    public @NonNull Session logout(@NonNull LogoutReason reason) {
        Objects.requireNonNull(reason, "LogoutReason cannot be null");

        SessionState targetState = reason == LogoutReason.FORCED ? SessionState.FORCED_LOGOUT : SessionState.LOGGED_OUT;

        this.validateStateTransition(targetState);

        LogoutInfo newLogoutInfo = LogoutInfo.builder()
                .logoutAt(Instant.now())
                .logoutReason(reason)
                .build();

        return this.toBuilder()
                .state(targetState)
                .logoutInfo(newLogoutInfo)
                .build();
    }

    public @NonNull Session updateActivity() {
        if (this.state != SessionState.ACTIVE) {
            throw new SessionInvalidStateException();
        }

        SessionMetadata updatedMetadata = SessionMetadata.builder()
                .ipAddress(this.metadata.getIpAddress())
                .userAgent(this.metadata.getUserAgent())
                .createdAt(this.metadata.getCreatedAt())
                .expiresAt(this.metadata.getExpiresAt())
                .lastActivityAt(Instant.now())
                .build();

        return this.toBuilder()
                .metadata(updatedMetadata)
                .build();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.metadata.getExpiresAt()) ||
                this.state == SessionState.EXPIRED;
    }

    public boolean isActive() {
        return this.state == SessionState.ACTIVE && !this.isExpired();
    }

    public @NonNull Duration getRemainingTime() {
        if (!this.isActive()) {
            return Duration.ZERO;
        }
        return Duration.between(Instant.now(), this.metadata.getExpiresAt());
    }

    private void validateStateTransition(@NonNull SessionState newState) {
        if (this.state == SessionState.LOGGED_OUT || this.state == SessionState.FORCED_LOGOUT) {
            throw new SessionInvalidStateException();
        }
        if (this.state == SessionState.EXPIRED && newState != SessionState.LOGGED_OUT) {
            throw new SessionInvalidStateException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Session session = (Session) o;
        return Objects.equals(this.sessionId, session.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sessionId);
    }

    @Override
    public @NonNull String toString() {
        return "Session{" +
                "sessionId=" + this.sessionId +
                ", userId=" + this.userId +
                ", state=" + this.state +
                '}';
    }
}
