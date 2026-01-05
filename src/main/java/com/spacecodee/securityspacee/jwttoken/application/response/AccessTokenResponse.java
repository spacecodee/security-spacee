package com.spacecodee.securityspacee.jwttoken.application.response;

public record AccessTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn) {
}
