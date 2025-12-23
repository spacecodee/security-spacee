package com.spacecodee.securityspacee.session.domain.valueobject;

import org.jspecify.annotations.NonNull;

import lombok.Builder;

@Builder
public record DeviceInfo(
        @NonNull String deviceName,
        @NonNull String browser,
        @NonNull String os,
        @NonNull String deviceType) {

    public @NonNull String friendlyName() {
        return this.deviceName + " - " + this.browser;
    }
}
