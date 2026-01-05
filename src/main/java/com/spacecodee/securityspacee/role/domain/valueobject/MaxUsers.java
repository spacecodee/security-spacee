package com.spacecodee.securityspacee.role.domain.valueobject;

import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Getter;

@Getter
public final class MaxUsers {

    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 1_000_000;
    private static final boolean IS_UNLIMITED = false;

    private final Integer value;

    private MaxUsers(Integer value) {
        this.value = this.validate(value);
    }

    @Contract("_ -> new")
    public static @NonNull MaxUsers of(Integer value) {
        return new MaxUsers(value);
    }

    @Contract("null -> fail")
    private @NonNull Integer validate(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("role.validation.max_users.required");
        }

        if (value < MIN_VALUE) {
            throw new IllegalArgumentException("role.validation.max_users.min_value");
        }

        if (value > MAX_VALUE) {
            throw new IllegalArgumentException("role.validation.max_users.max_value");
        }

        return value;
    }

    public boolean isUnlimited() {
        return IS_UNLIMITED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MaxUsers maxUsers = (MaxUsers) o;
        return Objects.equals(this.value, maxUsers.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
