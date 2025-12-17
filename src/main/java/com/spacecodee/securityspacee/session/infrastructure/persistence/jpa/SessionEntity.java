package com.spacecodee.securityspacee.session.infrastructure.persistence.jpa;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_session")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public final class SessionEntity {

    @Id
    @Column(name = "session_id", nullable = false, length = 255)
    private String sessionId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "session_token", nullable = false, unique = true, length = 500)
    private String sessionToken;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "last_activity_at", nullable = false)
    private Instant lastActivityAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "logout_at")
    private Instant logoutAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "logout_reason")
    private LogoutReasonDb logoutReason;

    public @NonNull SessionEntity withSessionId(@NonNull String sessionId) {
        return this.toBuilder().sessionId(sessionId).build();
    }

    public @NonNull SessionEntity withUserId(@NonNull Integer userId) {
        return this.toBuilder().userId(userId).build();
    }

    public @NonNull SessionEntity withSessionToken(@NonNull String sessionToken) {
        return this.toBuilder().sessionToken(sessionToken).build();
    }

    public @NonNull SessionEntity withIpAddress(String ipAddress) {
        return this.toBuilder().ipAddress(ipAddress).build();
    }

    public @NonNull SessionEntity withUserAgent(String userAgent) {
        return this.toBuilder().userAgent(userAgent).build();
    }

    public @NonNull SessionEntity withCreatedAt(@NonNull Instant createdAt) {
        return this.toBuilder().createdAt(createdAt).build();
    }

    public @NonNull SessionEntity withExpiresAt(@NonNull Instant expiresAt) {
        return this.toBuilder().expiresAt(expiresAt).build();
    }

    public @NonNull SessionEntity withLastActivityAt(@NonNull Instant lastActivityAt) {
        return this.toBuilder().lastActivityAt(lastActivityAt).build();
    }

    public @NonNull SessionEntity withIsActive(@NonNull Boolean isActive) {
        return this.toBuilder().isActive(isActive).build();
    }

    public @NonNull SessionEntity withLogoutAt(Instant logoutAt) {
        return this.toBuilder().logoutAt(logoutAt).build();
    }

    public @NonNull SessionEntity withLogoutReason(LogoutReasonDb logoutReason) {
        return this.toBuilder().logoutReason(logoutReason).build();
    }
}
