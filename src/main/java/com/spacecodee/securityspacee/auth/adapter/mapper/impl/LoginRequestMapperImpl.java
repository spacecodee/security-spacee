package com.spacecodee.securityspacee.auth.adapter.mapper.impl;

import com.spacecodee.securityspacee.auth.adapter.mapper.ILoginRequestMapper;
import com.spacecodee.securityspacee.auth.adapter.request.LoginRequest;
import com.spacecodee.securityspacee.auth.application.command.LoginCommand;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;

public final class LoginRequestMapperImpl implements ILoginRequestMapper {

    @Override
    public @NonNull LoginCommand toCommand(@NonNull LoginRequest request, HttpServletRequest servletRequest) {
        String ipAddress = extractIpAddress(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");

        return new LoginCommand(
                request.usernameOrEmail(),
                request.password(),
                ipAddress,
                userAgent);
    }

    private String extractIpAddress(@NonNull HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
