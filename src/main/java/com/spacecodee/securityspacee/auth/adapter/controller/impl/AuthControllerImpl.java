package com.spacecodee.securityspacee.auth.adapter.controller.impl;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.spacecodee.securityspacee.auth.adapter.controller.IAuthController;
import com.spacecodee.securityspacee.auth.adapter.mapper.ILoginRequestMapper;
import com.spacecodee.securityspacee.auth.adapter.mapper.IRefreshTokenRequestMapper;
import com.spacecodee.securityspacee.auth.adapter.request.LoginRequest;
import com.spacecodee.securityspacee.auth.adapter.request.RefreshTokenRequest;
import com.spacecodee.securityspacee.auth.application.command.LoginCommand;
import com.spacecodee.securityspacee.auth.application.port.in.ILoginUseCase;
import com.spacecodee.securityspacee.auth.application.response.AuthenticationResponse;
import com.spacecodee.securityspacee.jwttoken.application.command.RefreshTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRefreshTokenUseCase;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenPairResponse;
import com.spacecodee.securityspacee.session.application.command.LogoutCommand;
import com.spacecodee.securityspacee.session.application.port.in.ILogoutUseCase;
import com.spacecodee.securityspacee.session.application.response.LogoutResponse;
import com.spacecodee.securityspacee.session.domain.exception.MissingAuthenticationContextException;
import com.spacecodee.securityspacee.shared.adapter.in.web.dto.ApiDataResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public final class AuthControllerImpl implements IAuthController {

    private static final String SESSION_ID_HEADER = "X-Session-Id";

    private final ILoginUseCase loginUseCase;
    private final IRefreshTokenUseCase refreshTokenUseCase;
    private final ILogoutUseCase logoutUseCase;
    private final ILoginRequestMapper loginRequestMapper;
    private final IRefreshTokenRequestMapper refreshTokenRequestMapper;

    public AuthControllerImpl(
            ILoginUseCase loginUseCase,
            IRefreshTokenUseCase refreshTokenUseCase,
            ILogoutUseCase logoutUseCase,
            ILoginRequestMapper loginRequestMapper,
            IRefreshTokenRequestMapper refreshTokenRequestMapper) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.loginRequestMapper = loginRequestMapper;
        this.refreshTokenRequestMapper = refreshTokenRequestMapper;
    }

    @Override
    public @NonNull ResponseEntity<ApiDataResponse<Object>> login(LoginRequest request,
                                                                  HttpServletRequest servletRequest) {
        LoginCommand command = this.loginRequestMapper.toCommand(request, servletRequest);

        AuthenticationResponse response = this.loginUseCase.execute(command);

        ApiDataResponse<Object> apiResponse = ApiDataResponse.success(
                "auth.login.success",
                response,
                Instant.now());

        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public @NonNull ResponseEntity<ApiDataResponse<Object>> refresh(RefreshTokenRequest request,
                                                                    HttpServletRequest servletRequest) {
        RefreshTokenCommand command = this.refreshTokenRequestMapper.toCommand(request, servletRequest);

        TokenPairResponse response = this.refreshTokenUseCase.execute(command);

        ApiDataResponse<Object> apiResponse = ApiDataResponse.success(
                "auth.refresh.success",
                response,
                Instant.now());

        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public @NonNull ResponseEntity<ApiDataResponse<Object>> logout(HttpServletRequest servletRequest) {
        Integer userId = this.extractUserIdFromSecurityContext();
        String sessionId = this.extractSessionId(servletRequest);

        LogoutCommand command = LogoutCommand.builder()
                .sessionId(sessionId)
                .userId(userId)
                .reason("manual")
                .build();

        LogoutResponse response = this.logoutUseCase.execute(command);

        ApiDataResponse<Object> apiResponse = ApiDataResponse.success(
                "auth.logout.success",
                response,
                Instant.now());

        return ResponseEntity.ok(apiResponse);
    }

    private @NonNull Integer extractUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new MissingAuthenticationContextException();
        }
        return Integer.valueOf(authentication.getName());
    }

    private @NonNull String extractSessionId(@NonNull HttpServletRequest request) {
        String sessionId = request.getHeader(SESSION_ID_HEADER);
        if (sessionId == null || sessionId.isEmpty()) {
            throw new MissingAuthenticationContextException();
        }
        return sessionId;
    }
}
