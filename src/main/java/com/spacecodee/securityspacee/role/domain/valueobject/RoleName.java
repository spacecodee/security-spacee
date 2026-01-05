package com.spacecodee.securityspacee.role.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Getter;

@Getter
public final class RoleName {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 50;
    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Z0-9_]{3,50}$");

    private final String value;

    private RoleName(String rawValue) {
        this.value = this.validate(rawValue);
    }

    @Contract("_ -> new")
    public static @NonNull RoleName of(String rawValue) {
        return new RoleName(rawValue);
    }

    @Contract("null -> fail")
    private @NonNull String validate(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("role.validation.role_name.required");
        }

        String trimmed = rawValue.trim().toUpperCase();

        if (trimmed.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("role.validation.role_name.min_length");
        }

        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("role.validation.role_name.max_length");
        }

        if (!VALID_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("role.validation.role_name.invalid_format");
        }

        return trimmed;
    }

    public boolean isSystemRole() {
        return this.value.equals("ADMIN") || this.value.equals("USER") || this.value.equals("AUDITOR");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RoleName roleName = (RoleName) o;
        return Objects.equals(this.value, roleName.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
