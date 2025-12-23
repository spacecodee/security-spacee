package com.spacecodee.securityspacee.auth.infrastructure.adapter;

import java.time.Instant;
import java.util.Optional;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.auth.application.port.out.IUserAuthenticationPort;
import com.spacecodee.securityspacee.shared.config.properties.SecurityProperties;
import com.spacecodee.securityspacee.user.domain.model.User;
import com.spacecodee.securityspacee.user.domain.repository.IUserRepository;
import com.spacecodee.securityspacee.user.domain.valueobject.Email;
import com.spacecodee.securityspacee.user.domain.valueobject.Username;

public final class UserAuthenticationAdapter implements IUserAuthenticationPort {

    private final IUserRepository userRepository;
    private final SecurityProperties securityProperties;

    public UserAuthenticationAdapter(
            @NonNull IUserRepository userRepository,
            @NonNull SecurityProperties securityProperties) {
        this.userRepository = userRepository;
        this.securityProperties = securityProperties;
    }

    @Override
    public @NonNull Optional<User> findByUsernameOrEmail(@NonNull String usernameOrEmail) {
        if (usernameOrEmail.contains("@")) {
            return this.userRepository.findByEmail(Email.of(usernameOrEmail));
        }
        return this.userRepository.findByUsername(Username.of(usernameOrEmail));
    }

    @Override
    public void incrementFailedAttempts(@NonNull Integer userId) {
        Optional<User> userOpt = this.userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();
        User updatedUser = user.incrementFailedLoginAttempts(this.securityProperties.maxLoginAttempts());
        this.userRepository.save(updatedUser);
    }

    @Override
    public void resetFailedAttempts(@NonNull Integer userId) {
        Optional<User> userOpt = this.userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();
        User updatedUser = user.unlockAccount();
        this.userRepository.save(updatedUser);
    }

    @Override
    public void lockAccount(@NonNull Integer userId, @NonNull Instant lockedUntil) {
        Optional<User> userOpt = this.userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();
        long lockDurationMs = lockedUntil.toEpochMilli() - Instant.now().toEpochMilli();
        User lockedUser = user.lockAccount(lockDurationMs);
        this.userRepository.save(lockedUser);
    }

    @Override
    public void updateLastLogin(@NonNull Integer userId, @NonNull Instant loginTimestamp) {
        Optional<User> userOpt = this.userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();
        User updatedUser = user.recordSuccessfulLogin();
        this.userRepository.save(updatedUser);
    }
}
