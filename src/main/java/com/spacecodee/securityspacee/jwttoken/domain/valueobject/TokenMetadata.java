package com.spacecodee.securityspacee.jwttoken.domain.valueobject;

import java.time.Instant;

import org.jspecify.annotations.Nullable;

import lombok.Builder;

@Builder
public record TokenMetadata(
        @Nullable Integer usageCount,
        @Nullable Instant lastAccessAt,
        Instant issuedAt) {
}
