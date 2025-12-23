package com.spacecodee.securityspacee.shared.infrastructure.adapter.out;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.application.port.out.IClientIpExtractorPort;

import jakarta.servlet.http.HttpServletRequest;

public final class ClientIpExtractorAdapter implements IClientIpExtractorPort {

    @Override
    public @NonNull String extractClientIp(@NonNull HttpServletRequest request) {
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
