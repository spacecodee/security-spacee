package com.spacecodee.securityspacee.auth.application.command;

public record LoginCommand(
        String usernameOrEmail,
        String password,
        String ipAddress,
        String userAgent) {
}
