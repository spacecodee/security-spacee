package com.spacecodee.securityspacee.user.domain.event;

import java.time.Instant;
import java.util.Objects;

import com.spacecodee.securityspacee.user.domain.valueobject.UserType;

import lombok.Getter;

@Getter
public final class UserRegisteredEvent {

    private final Integer userId;
    private final String username;
    private final String email;
    private final UserType userType;
    private final Instant occurredOn;

    public UserRegisteredEvent(Integer userId, String username, String email, UserType userType) {
        this.userId = Objects.requireNonNull(userId);
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
        this.userType = Objects.requireNonNull(userType);
        this.occurredOn = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserRegisteredEvent that = (UserRegisteredEvent) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, occurredOn);
    }

    @Override
    public String toString() {
        return "UserRegisteredEvent{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", userType=" + userType +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
