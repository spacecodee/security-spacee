package com.spacecodee.securityspacee.session.infrastructure.service;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.port.out.IDeviceFingerprintService;
import com.spacecodee.securityspacee.session.domain.valueobject.DeviceFingerprint;

public final class DeviceFingerprintServiceAdapter implements IDeviceFingerprintService {

    @Override
    public @NonNull DeviceFingerprint generate(@NonNull String ipAddress, @NonNull String userAgent) {
        return DeviceFingerprint.from(ipAddress, userAgent);
    }

    @Override
    public boolean matches(@NonNull DeviceFingerprint fingerprint1, @NonNull DeviceFingerprint fingerprint2) {
        return fingerprint1.value().equals(fingerprint2.value());
    }
}
