package com.spacecodee.securityspacee.session.domain.valueobject;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import lombok.Builder;

@Builder
public record Location(
        @NonNull String city,
        @NonNull String country,
        @NonNull String countryCode,
        @Nullable Double latitude,
        @Nullable Double longitude) {

    public @NonNull String friendlyName() {
        return this.city + ", " + this.country;
    }

    public boolean isSameCountry(@NonNull Location other) {
        return this.countryCode.equals(other.countryCode);
    }
}
