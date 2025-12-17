package com.spacecodee.securityspacee.jwttoken.application.response;

public record TokenPairResponse(
        String accessToken,
        String refreshToken,
        long expiresIn) {
}
