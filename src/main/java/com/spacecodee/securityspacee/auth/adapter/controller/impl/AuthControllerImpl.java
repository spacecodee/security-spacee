package com.spacecodee.securityspacee.auth.adapter.controller.impl;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.spacecodee.securityspacee.auth.adapter.controller.IAuthController;
import com.spacecodee.securityspacee.auth.adapter.mapper.ILoginRequestMapper;
import com.spacecodee.securityspacee.auth.adapter.request.LoginRequest;
import com.spacecodee.securityspacee.auth.application.command.LoginCommand;
import com.spacecodee.securityspacee.auth.application.port.in.ILoginUseCase;
import com.spacecodee.securityspacee.auth.application.response.AuthenticationResponse;
import com.spacecodee.securityspacee.shared.adapter.in.web.dto.ApiDataResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public final class AuthControllerImpl implements IAuthController {

    private final ILoginUseCase loginUseCase;
    private final ILoginRequestMapper loginRequestMapper;

    public AuthControllerImpl(ILoginUseCase loginUseCase, ILoginRequestMapper loginRequestMapper) {
        this.loginUseCase = loginUseCase;
        this.loginRequestMapper = loginRequestMapper;
    }

    @Override
    public @NonNull ResponseEntity<ApiDataResponse<Object>> login(LoginRequest request, HttpServletRequest servletRequest) {
        LoginCommand command = loginRequestMapper.toCommand(request, servletRequest);

        AuthenticationResponse response = loginUseCase.execute(command);

        ApiDataResponse<Object> apiResponse = ApiDataResponse.success(
                "auth.login.success",
                response,
                Instant.now());

        return ResponseEntity.ok(apiResponse);
    }
}
