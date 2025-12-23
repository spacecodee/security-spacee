package com.spacecodee.securityspacee.session.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Getter;

@Getter
public final class SessionToken {

    private final UUID value;

    private SessionToken(UUID value) {
        this.value = Objects.requireNonNull(value, "SessionToken value cannot be null");
    }

    @Contract(" -> new")
    public static @NonNull SessionToken generate() {
        return new SessionToken(UUID.randomUUID());
    }

    @Contract("_ -> new")
    public static @NonNull SessionToken of(UUID value) {
        return new SessionToken(value);
    }

    @Contract("_ -> new")
    public static @NonNull SessionToken parse(String value) {
        return new SessionToken(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SessionToken that = (SessionToken) o;
        return Objects.equals(this.value, that.value);
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
