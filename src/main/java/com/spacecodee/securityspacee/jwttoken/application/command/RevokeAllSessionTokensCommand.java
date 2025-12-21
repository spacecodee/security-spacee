package com.spacecodee.securityspacee.jwttoken.application.command;

public record RevokeAllSessionTokensCommand(String sessionId, Integer revokedBy, String reason) {
}
