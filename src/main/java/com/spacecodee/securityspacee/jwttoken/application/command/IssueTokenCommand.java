package com.spacecodee.securityspacee.jwttoken.application.command;

import java.util.List;

public record IssueTokenCommand(
        Integer userId,
        String username,
        String sessionId,
        List<String> roles,
        String ipAddress,
        String userAgent) {
}
