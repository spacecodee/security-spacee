package com.spacecodee.securityspacee.session.adapter.controller.impl;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.spacecodee.securityspacee.session.adapter.controller.ISessionController;
import com.spacecodee.securityspacee.session.adapter.mapper.ILogoutRemoteSessionMapper;
import com.spacecodee.securityspacee.session.application.command.GetActiveSessionsQuery;
import com.spacecodee.securityspacee.session.application.command.LogoutRemoteSessionCommand;
import com.spacecodee.securityspacee.session.application.port.in.IGetActiveSessionsUseCase;
import com.spacecodee.securityspacee.session.application.port.in.ILogoutRemoteSessionUseCase;
import com.spacecodee.securityspacee.session.application.response.ActiveSessionsResponse;
import com.spacecodee.securityspacee.session.domain.exception.MissingAuthenticationContextException;
import com.spacecodee.securityspacee.shared.adapter.in.web.dto.ApiDataResponse;
import com.spacecodee.securityspacee.shared.adapter.in.web.dto.ApiPlainResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public final class SessionControllerImpl implements ISessionController {

    private static final String SESSION_ID_HEADER = "X-Session-Id";

    private final IGetActiveSessionsUseCase getActiveSessionsUseCase;
    private final ILogoutRemoteSessionUseCase logoutRemoteSessionUseCase;
    private final ILogoutRemoteSessionMapper logoutRemoteSessionMapper;

    public SessionControllerImpl(
            IGetActiveSessionsUseCase getActiveSessionsUseCase,
            ILogoutRemoteSessionUseCase logoutRemoteSessionUseCase,
            ILogoutRemoteSessionMapper logoutRemoteSessionMapper) {
        this.getActiveSessionsUseCase = getActiveSessionsUseCase;
        this.logoutRemoteSessionUseCase = logoutRemoteSessionUseCase;
        this.logoutRemoteSessionMapper = logoutRemoteSessionMapper;
    }

    @Override
    public @NonNull ResponseEntity<ApiDataResponse<Object>> getActiveSessions(HttpServletRequest servletRequest) {
        Integer userId = this.extractUserIdFromSecurityContext();
        String currentSessionId = this.extractCurrentSessionId(servletRequest);

        GetActiveSessionsQuery query = new GetActiveSessionsQuery(userId, currentSessionId);

        ActiveSessionsResponse response = this.getActiveSessionsUseCase.execute(query);

        ApiDataResponse<Object> apiResponse = ApiDataResponse.success(
                "session.list.success",
                response,
                Instant.now());

        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public @NonNull ResponseEntity<ApiPlainResponse> logoutRemoteSession(String sessionId) {
        Integer userId = this.extractUserIdFromSecurityContext();

        LogoutRemoteSessionCommand command = this.logoutRemoteSessionMapper.toCommand(userId, sessionId);

        this.logoutRemoteSessionUseCase.execute(command);

        ApiPlainResponse apiResponse = ApiPlainResponse.success(
                "session.logout.remote.success",
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

    private @NonNull String extractCurrentSessionId(@NonNull HttpServletRequest request) {
        String sessionId = request.getHeader(SESSION_ID_HEADER);
        return sessionId != null ? sessionId : "";
    }
}
