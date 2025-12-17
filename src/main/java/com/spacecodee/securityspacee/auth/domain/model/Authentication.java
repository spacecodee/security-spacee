package com.spacecodee.securityspacee.auth.domain.model;

import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.auth.domain.valueobject.AuthenticationResult;
import com.spacecodee.securityspacee.auth.domain.valueobject.Credentials;

import lombok.Getter;

@Getter
public final class Authentication {

    private final Credentials credentials;
    private final AuthenticationResult result;

    private Authentication(Credentials credentials, AuthenticationResult result) {
        this.credentials = Objects.requireNonNull(credentials, "credentials cannot be null");
        this.result = result;
    }

    @Contract("_ -> new")
    public static @NonNull Authentication pending(Credentials credentials) {
        return new Authentication(credentials, null);
    }

    @Contract("_ -> new")
    public @NonNull Authentication succeed(AuthenticationResult result) {
        return new Authentication(this.credentials, result);
    }

    public boolean isSuccessful() {
        return result != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Authentication that = (Authentication) o;
        return Objects.equals(credentials, that.credentials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(credentials);
    }

    @Override
    public String toString() {
        return "Authentication{" +
                "credentials=" + credentials +
                ", isSuccessful=" + isSuccessful() +
                '}';
    }
}
