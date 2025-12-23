package com.spacecodee.securityspacee.jwttoken.application.command;

public record RevokeAllUserTokensCommand(Integer userId, Integer revokedBy, String reason) {
}
