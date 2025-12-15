package com.spacecodee.securityspacee.user.infrastructure.security;

import java.util.Objects;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.spacecodee.securityspacee.user.application.port.out.IPasswordEncoder;

public final class BCryptPasswordEncoderAdapter implements IPasswordEncoder {

    private static final int BCRYPT_STRENGTH = 12;

    private final BCryptPasswordEncoder bCryptEncoder;

    public BCryptPasswordEncoderAdapter() {
        this.bCryptEncoder = new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    @Override
    public String encode(String plainPassword) {
        Objects.requireNonNull(plainPassword, "user.validation.password.required");

        if (plainPassword.isBlank()) {
            throw new IllegalArgumentException("user.validation.password.blank");
        }

        return bCryptEncoder.encode(plainPassword);
    }

    @Override
    public boolean matches(String plainPassword, String hashedPassword) {
        Objects.requireNonNull(plainPassword);
        Objects.requireNonNull(hashedPassword);

        return bCryptEncoder.matches(plainPassword, hashedPassword);
    }
}
