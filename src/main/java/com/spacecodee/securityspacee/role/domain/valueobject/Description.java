package com.spacecodee.securityspacee.role.domain.valueobject;

import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Getter;

@Getter
public final class Description {

    private static final int MAX_LENGTH = 255;

    private final String value;

    private Description(String rawValue) {
        this.value = this.validate(rawValue);
    }

    @Contract("_ -> new")
    public static @NonNull Description of(String rawValue) {
        return new Description(rawValue);
    }

    @Contract("null -> fail")
    private @NonNull String validate(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("role.validation.description.required");
        }

        String trimmed = rawValue.trim();

        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("role.validation.description.max_length");
        }

        return trimmed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Description that = (Description) o;
        return Objects.equals(this.value, that.value);
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
