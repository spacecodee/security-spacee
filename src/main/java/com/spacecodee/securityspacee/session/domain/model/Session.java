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

    private static final String REASON_NULL_ERROR = "Reason cannot be null";

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

    public @NonNull Session logout(@NonNull String reason, @NonNull Instant logoutAt, @NonNull Integer loggedOutBy) {
        Objects.requireNonNull(reason, REASON_NULL_ERROR);
        Objects.requireNonNull(logoutAt, "LogoutAt cannot be null");
        Objects.requireNonNull(loggedOutBy, "LoggedOutBy cannot be null");

        if (!this.isActive()) {
            throw new SessionInvalidStateException();
        }

        LogoutReason logoutReason = LogoutReason.fromString(reason);
        SessionState targetState = logoutReason == LogoutReason.FORCED ? SessionState.FORCED_LOGOUT
                : SessionState.LOGGED_OUT;

        this.validateStateTransition(targetState);

        LogoutInfo newLogoutInfo = LogoutInfo.builder()
                .logoutAt(logoutAt)
                .logoutReason(logoutReason)
                .loggedOutBy(loggedOutBy)
                .build();

        return this.toBuilder()
                .state(targetState)
                .logoutInfo(newLogoutInfo)
                .build();
    }

    public @NonNull Session forceLogout(@NonNull String reason, @NonNull Instant forcedAt) {
        Objects.requireNonNull(reason, REASON_NULL_ERROR);
        Objects.requireNonNull(forcedAt, "ForcedAt cannot be null");

        this.validateStateTransition(SessionState.FORCED_LOGOUT);

        LogoutInfo newLogoutInfo = LogoutInfo.builder()
                .logoutAt(forcedAt)
                .logoutReason(LogoutReason.FORCED)
                .build();

        return this.toBuilder()
                .state(SessionState.FORCED_LOGOUT)
                .logoutInfo(newLogoutInfo)
                .build();
    }

    public @NonNull Session forceLogout(@NonNull String reason, @NonNull Instant forcedAt, @NonNull Integer forcedBy) {
        Objects.requireNonNull(reason, REASON_NULL_ERROR);
        Objects.requireNonNull(forcedAt, "ForcedAt cannot be null");
        Objects.requireNonNull(forcedBy, "ForcedBy cannot be null");

        this.validateStateTransition(SessionState.FORCED_LOGOUT);

        LogoutInfo newLogoutInfo = LogoutInfo.builder()
                .logoutAt(forcedAt)
                .logoutReason(LogoutReason.FORCED)
                .loggedOutBy(forcedBy)
                .build();

        return this.toBuilder()
                .state(SessionState.FORCED_LOGOUT)
                .logoutInfo(newLogoutInfo)
                .build();
    }

    public boolean isOwnedBy(@NonNull Integer userId) {
        Objects.requireNonNull(userId, "userId cannot be null");
        return Objects.equals(this.userId, userId);
    }

    public @NonNull Session updateActivity() {
        if (this.state != SessionState.ACTIVE) {
            throw new SessionInvalidStateException();
        }

        SessionMetadata updatedMetadata = this.metadata.toBuilder()
                .lastActivityAt(Instant.now())
                .build();

        return this.toBuilder()
                .metadata(updatedMetadata)
                .build();
    }

    public @NonNull Session updateLastActivityAt(@NonNull Instant timestamp) {
        Objects.requireNonNull(timestamp, "timestamp cannot be null");

        if (!this.isActive()) {
            throw new SessionInvalidStateException();
        }

        SessionMetadata updatedMetadata = this.metadata.toBuilder()
                .lastActivityAt(timestamp)
                .build();

        return this.toBuilder()
                .metadata(updatedMetadata)
                .build();
    }

    public @NonNull Session extendExpiration(@NonNull Instant newExpiresAt) {
        Objects.requireNonNull(newExpiresAt, "newExpiresAt cannot be null");

        if (!this.isActive()) {
            throw new SessionInvalidStateException();
        }

        SessionMetadata updatedMetadata = this.metadata.toBuilder()
                .expiresAt(newExpiresAt)
                .build();

        return this.toBuilder()
                .metadata(updatedMetadata)
                .build();
    }

    public @NonNull Session expireByTimeout(@NonNull String reason, @NonNull Instant expiredAt) {
        Objects.requireNonNull(reason, "reason cannot be null");
        Objects.requireNonNull(expiredAt, "expiredAt cannot be null");

        this.validateStateTransition(SessionState.EXPIRED);

        LogoutInfo newLogoutInfo = LogoutInfo.builder()
                .logoutAt(expiredAt)
                .logoutReason(LogoutReason.TIMEOUT)
                .build();

        return this.toBuilder()
                .state(SessionState.EXPIRED)
                .logoutInfo(newLogoutInfo)
                .build();
    }

    public @NonNull Duration getIdleTime(@NonNull Instant currentTime) {
        Objects.requireNonNull(currentTime, "currentTime cannot be null or empty");
        return Duration.between(this.metadata.getLastActivityAt(), currentTime);
    }

    public @NonNull Duration getRemainingAbsoluteTime(@NonNull Instant currentTime) {
        Objects.requireNonNull(currentTime, "currentTime cannot be null, empty");
        return Duration.between(currentTime, this.metadata.getExpiresAt());
    }

    public boolean isIdleExpired(@NonNull Duration idleTimeout, @NonNull Instant currentTime) {
        Objects.requireNonNull(idleTimeout, "idleTimeout cannot be null");
        Objects.requireNonNull(currentTime, "currentTime cannot be null");
        return this.getIdleTime(currentTime).compareTo(idleTimeout) > 0;
    }

    public boolean isAbsoluteExpired(@NonNull Instant currentTime) {
        Objects.requireNonNull(currentTime, "currentTime cannot be null");
        return this.metadata.getExpiresAt().isBefore(currentTime);
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
