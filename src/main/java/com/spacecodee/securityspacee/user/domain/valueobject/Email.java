package com.spacecodee.securityspacee.user.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Getter;

@Getter
public final class Email {

    private static final int MAX_LENGTH = 255;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[\\w+&*-]+(?:\\.[\\w+&*-]+){0,10}@[\\w-]+(?:\\.[\\w-]+){1,5}$");

    private final String value;

    private Email(String rawValue) {
        this.value = validate(rawValue);
    }

    @Contract("_ -> new")
    public static @NonNull Email of(String rawValue) {
        return new Email(rawValue);
    }

    @Contract("null -> fail")
    private @NonNull String validate(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("user.validation.email.required");
        }

        String trimmed = rawValue.trim();

        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("user.validation.email.max_length");
        }

        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("user.validation.email.invalid_format");
        }

        return trimmed.toLowerCase();
    }

    public @NonNull String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }

    public @NonNull String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
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
