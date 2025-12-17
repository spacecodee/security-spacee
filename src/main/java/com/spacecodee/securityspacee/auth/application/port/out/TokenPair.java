package com.spacecodee.securityspacee.auth.application.port.out;

public record TokenPair(
        String accessToken,
        String refreshToken,
        Integer expiresIn) {
}
