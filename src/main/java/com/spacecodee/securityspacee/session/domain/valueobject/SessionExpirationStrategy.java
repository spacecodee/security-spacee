package com.spacecodee.securityspacee.session.domain.valueobject;

public enum SessionExpirationStrategy {
    FIFO,
    LIFO,
    LEAST_USED,
    EXPLICIT_LOGOUT
}
