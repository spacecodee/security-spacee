package com.spacecodee.securityspacee.user.application.command;

import java.time.LocalDate;

import com.spacecodee.securityspacee.user.domain.valueobject.UserType;

public record RegisterUserCommand(
        String username,
        String email,
        String password,
        UserType userType,
        String firstName,
        String lastName,
        String phoneNumber,
        String languageCode,
        String avatarUrl,
        String bio,
        String timezone,
        LocalDate dateOfBirth) {
}
