package com.spacecodee.securityspacee.session.adapter.controller.impl;

import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.spacecodee.securityspacee.session.adapter.controller.ISessionActivityController;
import com.spacecodee.securityspacee.session.adapter.mapper.ISessionStatusResponseMapper;
import com.spacecodee.securityspacee.session.adapter.response.HeartbeatResponse;
import com.spacecodee.securityspacee.session.adapter.response.SessionStatusResponse;
import com.spacecodee.securityspacee.session.application.command.CheckSessionExpirationQuery;
import com.spacecodee.securityspacee.session.application.port.in.ICheckSessionExpirationUseCase;
import com.spacecodee.securityspacee.session.application.response.SessionExpirationStatus;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;
import com.spacecodee.securityspacee.shared.adapter.in.web.dto.ApiDataResponse;
import com.spacecodee.securityspacee.shared.application.port.out.IMessageResolverPort;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public final class SessionActivityControllerImpl implements ISessionActivityController {

    private static final String SESSION_ID_ATTRIBUTE = "sessionId";

    private final ICheckSessionExpirationUseCase checkSessionExpirationUseCase;
    private final ISessionStatusResponseMapper statusResponseMapper;
    private final IMessageResolverPort messageResolverPort;
    private final HttpServletRequest httpServletRequest;

    public SessionActivityControllerImpl(
            @NonNull ICheckSessionExpirationUseCase checkSessionExpirationUseCase,
            @NonNull ISessionStatusResponseMapper statusResponseMapper,
            @NonNull IMessageResolverPort messageResolverPort,
            @NonNull HttpServletRequest httpServletRequest) {
        this.checkSessionExpirationUseCase = checkSessionExpirationUseCase;
        this.statusResponseMapper = statusResponseMapper;
        this.messageResolverPort = messageResolverPort;
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public @NonNull ResponseEntity<ApiDataResponse<HeartbeatResponse>> heartbeat() {
        SessionId sessionId = this.extractSessionId();

        SessionExpirationStatus status = this.checkSessionExpirationUseCase
                .execute(new CheckSessionExpirationQuery(sessionId));

        HeartbeatResponse response = HeartbeatResponse.builder()
                .status("alive")
                .expiresInSeconds(status.getRemainingIdleTime().toSeconds())
                .absoluteExpiresInSeconds(status.getRemainingAbsoluteTime().toSeconds())
                .message(this.messageResolverPort.getMessage("session.heartbeat.success"))
                .build();

        return ResponseEntity.ok(ApiDataResponse.success(
                this.messageResolverPort.getMessage("session.heartbeat.success"),
                response,
                java.time.Instant.now()));
    }

    @Override
    public @NonNull ResponseEntity<ApiDataResponse<SessionStatusResponse>> status() {
        SessionId sessionId = this.extractSessionId();

        SessionExpirationStatus status = this.checkSessionExpirationUseCase
                .execute(new CheckSessionExpirationQuery(sessionId));

        SessionStatusResponse response = this.statusResponseMapper.toResponse(status);

        return ResponseEntity.ok(ApiDataResponse.success(
                this.messageResolverPort.getMessage("session.status.retrieved"),
                response,
                java.time.Instant.now()));
    }

    private @NonNull SessionId extractSessionId() {
        Object sessionIdAttribute = this.httpServletRequest.getAttribute(SESSION_ID_ATTRIBUTE);

        if (sessionIdAttribute instanceof String sessionIdStr) {
            return SessionId.parse(sessionIdStr);
        }

        throw new com.spacecodee.securityspacee.session.domain.exception.MissingSessionIdException(
                this.messageResolverPort.getMessage("session.error.missing_session_id"));
    }
}
