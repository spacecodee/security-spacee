package com.spacecodee.securityspacee.jwttoken.application.command;

public record RefreshTokenCommand(
        String refreshToken,
        String ipAddress,
        String userAgent) {
}
