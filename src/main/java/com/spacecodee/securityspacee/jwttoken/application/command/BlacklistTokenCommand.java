package com.spacecodee.securityspacee.jwttoken.application.command;

import com.spacecodee.securityspacee.jwttoken.domain.valueobject.Jti;

public record BlacklistTokenCommand(Jti jti, Integer blacklistedBy, String reason) {
}
