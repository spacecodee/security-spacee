package com.spacecodee.securityspacee.jwttoken.application.command;

public record RevokeTokenCommand(
        String jti,
        Integer revokedBy,
        String reason) {
}
