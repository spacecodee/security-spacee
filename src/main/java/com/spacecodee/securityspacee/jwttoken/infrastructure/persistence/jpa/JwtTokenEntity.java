package com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.jpa;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "jwt_token")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "jti", nullable = false, unique = true)
    private @NonNull String jti;

    @Column(name = "token", nullable = false, columnDefinition = "TEXT")
    private @NonNull String token;

    @Column(name = "token_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private @NonNull TokenType tokenType;

    @Column(name = "user_id", nullable = false)
    private @NonNull Integer userId;

    @Column(name = "session_id")
    private @Nullable String sessionId;

    @Column(name = "is_valid", nullable = false)
    private @NonNull Boolean isValid;

    @Column(name = "is_revoked", nullable = false)
    private @NonNull Boolean isRevoked;

    @Column(name = "state", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private @NonNull TokenState state;

    @Column(name = "issued_at", nullable = false)
    private @NonNull Instant issuedAt;

    @Column(name = "expiry_date", nullable = false)
    private @NonNull Instant expiryDate;

    @Column(name = "revoked_at")
    private @Nullable Instant revokedAt;

    @Column(name = "revoked_reason")
    private @Nullable String revokedReason;

    @Column(name = "revoked_by")
    private @Nullable Integer revokedBy;

    @Column(name = "refresh_count", nullable = false)
    private @NonNull Integer refreshCount;

    @Column(name = "last_refresh_at")
    private @Nullable Instant lastRefreshAt;

    @Column(name = "previous_token_jti")
    private @Nullable String previousTokenJti;

    @Column(name = "usage_count", nullable = false)
    private @NonNull Integer usageCount;

    @Column(name = "last_access_at")
    private @Nullable Instant lastAccessAt;

    @Column(name = "last_operation", length = 100)
    private @Nullable String lastOperation;

    @Column(name = "client_ip", length = 45)
    private @Nullable String clientIp;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private @Nullable String userAgent;

    @Column(name = "created_at", nullable = false)
    private @NonNull Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private @NonNull Instant updatedAt;

}
