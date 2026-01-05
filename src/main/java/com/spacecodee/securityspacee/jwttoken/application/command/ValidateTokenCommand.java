package com.spacecodee.securityspacee.jwttoken.application.command;

import com.spacecodee.securityspacee.jwttoken.domain.valueobject.ValidationMode;

public record ValidateTokenCommand(
        String token,
        ValidationMode mode) {
}
