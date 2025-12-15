package com.spacecodee.securityspacee.user.adapter.mapper.impl;

import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.user.adapter.mapper.IUserRestMapper;
import com.spacecodee.securityspacee.user.adapter.request.RegisterUserRequest;
import com.spacecodee.securityspacee.user.application.command.RegisterUserCommand;
import com.spacecodee.securityspacee.user.domain.valueobject.UserType;

public final class UserRestMapperImpl implements IUserRestMapper {

    @Contract("_ -> new")
    @Override
    public @NonNull RegisterUserCommand toCommand(RegisterUserRequest request) {
        Objects.requireNonNull(request);

        UserType userType = request.userType() != null ? request.userType() : UserType.HUMAN;
        String languageCode = request.languageCode() != null ? request.languageCode() : "en";
        String timezone = request.timezone() != null ? request.timezone() : "UTC";

        return new RegisterUserCommand(
                request.username(),
                request.email(),
                request.password(),
                userType,
                request.firstName(),
                request.lastName(),
                request.phoneNumber(),
                languageCode,
                request.avatarUrl(),
                request.bio(),
                timezone,
                request.dateOfBirth());
    }
}
