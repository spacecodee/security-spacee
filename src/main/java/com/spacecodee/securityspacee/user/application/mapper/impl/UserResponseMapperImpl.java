package com.spacecodee.securityspacee.user.application.mapper.impl;

import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.user.application.mapper.IUserResponseMapper;
import com.spacecodee.securityspacee.user.application.response.UserResponse;
import com.spacecodee.securityspacee.user.domain.model.User;

public final class UserResponseMapperImpl implements IUserResponseMapper {

    @Contract("_ -> new")
    @Override
    public @NonNull UserResponse toResponse(User user) {
        Objects.requireNonNull(user);

        String fullName = user.getProfile() != null
                ? user.getProfile().getFullName()
                : null;

        String languageCode = user.getProfile() != null
                ? user.getProfile().getLanguageCode()
                : "en";

        String timezone = user.getProfile() != null
                ? user.getProfile().getTimezone()
                : "UTC";

        return new UserResponse(
                user.getUserId(),
                user.getUsername().getValue(),
                user.getEmail().getValue(),
                user.getUserType(),
                user.isActive(),
                user.isEmailVerified(),
                fullName,
                languageCode,
                timezone,
                user.getCreatedAt());
    }
}
