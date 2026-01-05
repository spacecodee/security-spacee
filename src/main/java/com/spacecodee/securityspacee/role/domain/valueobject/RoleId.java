package com.spacecodee.securityspacee.role.domain.valueobject;

import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Getter;

@Getter
public final class RoleId {

    private final Integer value;

    private RoleId(Integer value) {
        this.value = Objects.requireNonNull(value, "role.validation.role_id.required");
    }

    @Contract("_ -> new")
    public static @NonNull RoleId of(Integer value) {
        return new RoleId(value);
    }

    @Contract("_ -> new")
    public static @NonNull RoleId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("role.validation.role_id.required");
        }

        try {
            return new RoleId(Integer.parseInt(value.trim()));
        } catch (NumberFormatException _) {
            throw new IllegalArgumentException("role.validation.role_id.invalid_format");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RoleId roleId = (RoleId) o;
        return Objects.equals(this.value, roleId.value);
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
