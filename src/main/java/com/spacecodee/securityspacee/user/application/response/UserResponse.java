package com.spacecodee.securityspacee.user.application.response;

import java.time.Instant;

import com.spacecodee.securityspacee.user.domain.valueobject.UserType;

public record UserResponse(
        Long userId,
        String username,
        String email,
        UserType userType,
        boolean isActive,
        boolean emailVerified,
        String fullName,
        String languageCode,
        String timezone,
        Instant createdAt) {
}
