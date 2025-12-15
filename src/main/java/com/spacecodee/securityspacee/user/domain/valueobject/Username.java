package com.spacecodee.securityspacee.user.domain.valueobject;

import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Getter;

@Getter
public final class Username {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;
    private static final String VALIDATION_PATTERN = "^\\w+$";

    private final String value;

    private Username(String rawValue) {
        this.value = validate(rawValue);
    }

    @Contract("_ -> new")
    public static @NonNull Username of(String rawValue) {
        return new Username(rawValue);
    }

    @Contract("null -> fail")
    private @NonNull String validate(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("user.validation.username.required");
        }

        String trimmed = rawValue.trim();

        if (trimmed.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("user.validation.username.min_length");
        }

        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("user.validation.username.max_length");
        }

        if (!trimmed.matches(VALIDATION_PATTERN)) {
            throw new IllegalArgumentException("user.validation.username.invalid_format");
        }

        if (trimmed.startsWith("_") || trimmed.endsWith("_")) {
            throw new IllegalArgumentException("user.validation.username.invalid_boundaries");
        }

        return trimmed.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Username username = (Username) o;
        return Objects.equals(value, username.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
