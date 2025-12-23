package com.spacecodee.securityspacee.auth.application.port.out;

import java.time.Instant;
import java.util.Optional;

import com.spacecodee.securityspacee.user.domain.model.User;

public interface IUserAuthenticationPort {

    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    void incrementFailedAttempts(Integer userId);

    void resetFailedAttempts(Integer userId);

    void lockAccount(Integer userId, Instant lockedUntil);

    void updateLastLogin(Integer userId, Instant loginTimestamp);
}
