package com.spacecodee.securityspacee.jwttoken.domain.valueobject;

public enum ValidationFailureReason {
    EXPIRED,
    INVALID_SIGNATURE,
    REVOKED,
    MALFORMED,
    BLACKLISTED,
    SESSION_INACTIVE,
    SESSION_EXPIRED,
    SESSION_NOT_FOUND
}
