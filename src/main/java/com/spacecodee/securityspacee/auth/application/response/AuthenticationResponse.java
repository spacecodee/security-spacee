package com.spacecodee.securityspacee.auth.application.response;

public record AuthenticationResponse(
        String accessToken,
        String refreshToken,
        Integer expiresIn,
        String tokenType,
        UserInfo user) {
}
