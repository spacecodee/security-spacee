package com.spacecodee.securityspacee.auth.infrastructure.adapter;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.auth.application.port.out.IPasswordValidator;
import com.spacecodee.securityspacee.user.application.port.out.IPasswordEncoder;

public final class PasswordValidatorAdapter implements IPasswordValidator {

    private final IPasswordEncoder passwordEncoder;

    public PasswordValidatorAdapter(@NonNull IPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean matches(@NonNull String rawPassword, @NonNull String hashedPassword) {
        return this.passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
