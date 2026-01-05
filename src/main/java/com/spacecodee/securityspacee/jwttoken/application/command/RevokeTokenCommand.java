package com.spacecodee.securityspacee.jwttoken.application.command;

import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;

public record RevokeTokenCommand(Jti jti, Integer revokedBy, String reason) {
}
