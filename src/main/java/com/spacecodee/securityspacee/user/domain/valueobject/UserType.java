package com.spacecodee.securityspacee.user.domain.valueobject;

public enum UserType {
    /**
     * Regular end-users with full profile information.
     * Requires: firstName, lastName, and other personal data.
     */
    HUMAN,

    /**
     * Internal system accounts for automated processes.
     * Profile: NOT allowed.
     */
    SYSTEM,

    /**
     * External service accounts for API/microservice communication.
     * Profile: NOT allowed.
     */
    SERVICE;

    public boolean requiresProfile() {
        return this == HUMAN;
    }

    public boolean isInteractiveUser() {
        return this == HUMAN;
    }
}
