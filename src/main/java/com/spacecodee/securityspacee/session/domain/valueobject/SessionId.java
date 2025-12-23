package com.spacecodee.securityspacee.session.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Getter;

@Getter
public final class SessionId {

    private final UUID value;

    private SessionId(UUID value) {
        this.value = Objects.requireNonNull(value, "SessionId value cannot be null");
    }

    @Contract(" -> new")
    public static @NonNull SessionId generate() {
        return new SessionId(UUID.randomUUID());
    }

    @Contract("_ -> new")
    public static @NonNull SessionId of(UUID value) {
        return new SessionId(value);
    }

    @Contract("_ -> new")
    public static @NonNull SessionId parse(String value) {
        return new SessionId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SessionId sessionId = (SessionId) o;
        return Objects.equals(this.value, sessionId.value);
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
