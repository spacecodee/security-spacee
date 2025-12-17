package com.spacecodee.securityspacee.auth.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.spacecodee.securityspacee.user.domain.valueobject.UserType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class LoginSuccessEvent {

    private final Integer userId;
    private final String username;
    private final String email;
    private final UserType userType;
    private final Instant loginTimestamp;
    private final String ipAddress;
    private final String userAgent;
    private final List<String> assignedRoles;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LoginSuccessEvent that = (LoginSuccessEvent) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(loginTimestamp, that.loginTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, loginTimestamp);
    }

    @Override
    public String toString() {
        return "LoginSuccessEvent{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType +
                ", loginTimestamp=" + loginTimestamp +
                ", assignedRoles=" + assignedRoles +
                '}';
    }
}
