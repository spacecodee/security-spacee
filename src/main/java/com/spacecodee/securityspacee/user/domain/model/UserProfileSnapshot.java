package com.spacecodee.securityspacee.user.domain.model;

import java.time.LocalDate;

public record UserProfileSnapshot(
        String firstName,
        String lastName,
        String phoneNumber,
        boolean phoneVerified,
        String languageCode,
        String avatarUrl,
        String bio,
        String timezone,
        LocalDate dateOfBirth) {
}
