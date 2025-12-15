package com.spacecodee.securityspacee.user.domain.valueobject;

import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Getter;

@Getter
public final class Password {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;

    private final String value;

    private Password(String rawValue, boolean isHashed) {
        this.value = isHashed ? validateHashed(rawValue) : validate(rawValue);
    }

    @Contract("_ -> new")
    public static @NonNull Password ofPlain(String rawValue) {
        return new Password(rawValue, false);
    }

    @Contract("_ -> new")
    public static @NonNull Password ofHashed(String hashedValue) {
        return new Password(hashedValue, true);
    }

    @Contract("null -> fail")
    private @NonNull String validateHashed(String hashedValue) {
        if (hashedValue == null || hashedValue.isBlank()) {
            throw new IllegalArgumentException("user.validation.password.required");
        }
        return hashedValue;
    }

    @Contract("null -> fail")
    private @NonNull String validate(String rawValue) {
        if (rawValue == null || rawValue.isEmpty()) {
            throw new IllegalArgumentException("user.validation.password.required");
        }

        if (rawValue.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("user.validation.password.min_length");
        }

        if (rawValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("user.validation.password.max_length");
        }

        return rawValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Password password = (Password) o;
        return Objects.equals(value, password.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "****** (masked)";
    }
}
