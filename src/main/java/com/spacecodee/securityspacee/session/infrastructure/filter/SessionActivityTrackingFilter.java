package com.spacecodee.securityspacee.session.infrastructure.filter;

import java.io.IOException;
import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;
import com.spacecodee.securityspacee.session.infrastructure.service.AsyncSessionActivityService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public final class SessionActivityTrackingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SessionActivityTrackingFilter.class);
    private static final String SESSION_ID_ATTRIBUTE = "sessionId";

    private final AsyncSessionActivityService sessionActivityService;

    public SessionActivityTrackingFilter(@NonNull AsyncSessionActivityService sessionActivityService) {
        this.sessionActivityService = sessionActivityService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } finally {
            this.updateSessionActivity(request);
        }
    }

    private void updateSessionActivity(@NonNull HttpServletRequest request) {
        Object sessionIdAttribute = request.getAttribute(SESSION_ID_ATTRIBUTE);

        if (sessionIdAttribute instanceof String sessionIdStr && !sessionIdStr.isBlank()) {
            try {
                SessionId sessionId = SessionId.parse(sessionIdStr);
                this.sessionActivityService.updateActivityAsync(sessionId, Instant.now());
            } catch (Exception e) {
                log.warn("Failed to update session activity for {}: {}", sessionIdStr, e.getMessage());
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/") ||
                path.startsWith("/actuator/health") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
    }
}
