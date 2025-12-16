package com.spacecodee.securityspacee.auth.domain.valueobject;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.spacecodee.securityspacee.user.domain.valueobject.UserType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class AuthenticationResult {

    private final Integer userId;
    private final String username;
    private final String email;
    private final UserType userType;
    private final boolean isAccountActive;
    private final boolean isEmailVerified;
    private final Instant lastLoginAt;
    private final List<String> assignedRoles;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AuthenticationResult that = (AuthenticationResult) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "AuthenticationResult{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType +
                ", isAccountActive=" + isAccountActive +
                ", isEmailVerified=" + isEmailVerified +
                ", assignedRoles=" + assignedRoles +
                '}';
    }
}
