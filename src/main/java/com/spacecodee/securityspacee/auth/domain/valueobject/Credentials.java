package com.spacecodee.securityspacee.auth.domain.valueobject;

import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Getter;

@Getter
public final class Credentials {

    private final String usernameOrEmail;
    private final String rawPassword;

    private Credentials(String usernameOrEmail, String rawPassword) {
        this.usernameOrEmail = Objects.requireNonNull(usernameOrEmail,
                "auth.validation.credentials.username_or_email.required");
        this.rawPassword = Objects.requireNonNull(rawPassword, "auth.validation.credentials.password.required");

        if (usernameOrEmail.isBlank()) {
            throw new IllegalArgumentException("auth.validation.credentials.username_or_email.blank");
        }

        if (rawPassword.isBlank()) {
            throw new IllegalArgumentException("auth.validation.credentials.password.blank");
        }
    }

    @Contract("_, _ -> new")
    public static @NonNull Credentials of(String usernameOrEmail, String rawPassword) {
        return new Credentials(usernameOrEmail, rawPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Credentials that = (Credentials) o;
        return Objects.equals(usernameOrEmail, that.usernameOrEmail) &&
                Objects.equals(rawPassword, that.rawPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usernameOrEmail, rawPassword);
    }

    @Override
    public String toString() {
        return "Credentials{usernameOrEmail='" + usernameOrEmail + "', rawPassword='[PROTECTED]'}";
    }
}
