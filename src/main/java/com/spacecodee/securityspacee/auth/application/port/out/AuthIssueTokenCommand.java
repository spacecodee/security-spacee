package com.spacecodee.securityspacee.auth.application.port.out;

import java.util.List;

import com.spacecodee.securityspacee.user.domain.valueobject.UserType;

public record AuthIssueTokenCommand(
        Integer userId,
        String username,
        String email,
        UserType userType,
        List<String> roles) {
}
