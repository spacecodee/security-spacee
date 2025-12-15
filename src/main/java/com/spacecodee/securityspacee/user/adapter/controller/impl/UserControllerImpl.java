package com.spacecodee.securityspacee.user.adapter.controller.impl;

import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.spacecodee.securityspacee.user.adapter.controller.IUserController;
import com.spacecodee.securityspacee.user.adapter.mapper.IUserRestMapper;
import com.spacecodee.securityspacee.user.adapter.request.RegisterUserRequest;
import com.spacecodee.securityspacee.user.application.command.RegisterUserCommand;
import com.spacecodee.securityspacee.user.application.port.in.IRegisterUserUseCase;
import com.spacecodee.securityspacee.user.application.response.UserResponse;

import jakarta.validation.Valid;

@RestController
public final class UserControllerImpl implements IUserController {

    private static final Logger log = LoggerFactory.getLogger(UserControllerImpl.class);

    private final IRegisterUserUseCase registerUserUseCase;
    private final IUserRestMapper restMapper;

    public UserControllerImpl(
            IRegisterUserUseCase registerUserUseCase,
            IUserRestMapper restMapper) {
        this.registerUserUseCase = Objects.requireNonNull(registerUserUseCase);
        this.restMapper = Objects.requireNonNull(restMapper);
    }

    @Override
    public @NonNull ResponseEntity<UserResponse> registerUser(@Valid @NonNull RegisterUserRequest request) {
        log.info("POST /api/v1/users/register - username: {}", request.username());

        RegisterUserCommand command = restMapper.toCommand(request);

        UserResponse response = registerUserUseCase.register(command);

        log.info("User registered successfully - userId: {}, username: {}",
                response.userId(), response.username());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
