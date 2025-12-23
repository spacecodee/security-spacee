package com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.mapper.impl;

import java.time.Instant;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.RevocationInfo;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenState;
import com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.jpa.JwtTokenEntity;
import com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.mapper.IJwtTokenPersistenceMapper;

public final class JwtTokenPersistenceMapperImpl implements IJwtTokenPersistenceMapper {

    @Override
    @NonNull
    public JwtTokenEntity toEntity(@NonNull JwtToken jwtToken) {
        Instant now = Instant.now();
        boolean isRevoked = jwtToken.getState() == TokenState.REVOKED;

        JwtTokenEntity.JwtTokenEntityBuilder builder = JwtTokenEntity.builder()
                .jti(jwtToken.getJti().getValue().toString())
                .token(jwtToken.getRawToken())
                .tokenType(jwtToken.getTokenType())
                .userId(jwtToken.getUserId())
                .sessionId(jwtToken.getSessionId())
                .isValid(jwtToken.getState() == TokenState.ACTIVE)
                .isRevoked(isRevoked)
                .state(jwtToken.getState())
                .issuedAt(jwtToken.getIssuedAt())
                .expiryDate(jwtToken.getExpiryDate())
                .usageCount(jwtToken.getUsageCount() != null ? jwtToken.getUsageCount() : 0)
                .refreshCount(jwtToken.getRefreshCount() != null ? jwtToken.getRefreshCount() : 0)
                .lastAccessAt(jwtToken.getLastAccessAt())
                .lastRefreshAt(jwtToken.getLastRefreshAt())
                .previousTokenJti(null) // Not tracked in domain yet
                .lastOperation(null) // Not tracked in domain yet
                .clientIp(jwtToken.getClientIp())
                .userAgent(jwtToken.getUserAgent())
                .createdAt(now)
                .updatedAt(now);

        // Map RevocationInfo if present
        if (jwtToken.getRevocationInfo() != null) {
            RevocationInfo revocationInfo = jwtToken.getRevocationInfo();
            builder.revokedAt(revocationInfo.getRevokedAt())
                    .revokedBy(revocationInfo.getRevokedBy())
                    .revokedReason(revocationInfo.getReason());
        }

        return builder.build();
    }

    @Override
    @NonNull
    public JwtToken toDomain(@NonNull JwtTokenEntity entity) {
        JwtToken.JwtTokenBuilder builder = JwtToken.builder()
                .jti(Jti.parse(entity.getJti()))
                .rawToken(entity.getToken())
                .tokenType(entity.getTokenType())
                .userId(entity.getUserId())
                .sessionId(entity.getSessionId())
                .state(entity.getState())
                .issuedAt(entity.getIssuedAt())
                .expiryDate(entity.getExpiryDate())
                .usageCount(entity.getUsageCount())
                .refreshCount(entity.getRefreshCount())
                .lastAccessAt(entity.getLastAccessAt())
                .lastRefreshAt(entity.getLastRefreshAt())
                .clientIp(entity.getClientIp())
                .userAgent(entity.getUserAgent())
                .claims(null); // Claims will be parsed from rawToken when needed

        // Reconstruct RevocationInfo if token was revoked
        if (entity.getRevokedAt() != null) {
            RevocationInfo revocationInfo = RevocationInfo.builder()
                    .revokedAt(entity.getRevokedAt())
                    .revokedBy(entity.getRevokedBy())
                    .reason(entity.getRevokedReason())
                    .build();
            builder.revocationInfo(revocationInfo);
        }

        return builder.build();
    }
}
