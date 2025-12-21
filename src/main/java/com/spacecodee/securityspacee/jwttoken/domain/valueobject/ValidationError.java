package com.spacecodee.securityspacee.jwttoken.domain.valueobject;

public record ValidationError(
        String code,
        String message) {
}
