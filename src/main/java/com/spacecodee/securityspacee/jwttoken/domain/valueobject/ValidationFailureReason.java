package com.spacecodee.securityspacee.jwttoken.domain.valueobject;

public enum ValidationFailureReason {
    EXPIRED,
    INVALID_SIGNATURE,
    REVOKED,
    MALFORMED,
    BLACKLISTED
}
