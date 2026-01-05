package com.spacecodee.securityspacee.jwttoken.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Getter;

@Getter
public final class Jti {

    private final UUID value;

    private Jti(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    @Contract(" -> new")
    public static @NonNull Jti generate() {
        return new Jti(UUID.randomUUID());
    }

    @Contract("_ -> new")
    public static @NonNull Jti of(UUID value) {
        return new Jti(value);
    }

    @Contract("_ -> new")
    public static @NonNull Jti parse(String value) {
        return new Jti(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Jti jti = (Jti) o;
        return Objects.equals(this.value, jti.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
