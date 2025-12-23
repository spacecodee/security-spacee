package com.spacecodee.securityspacee.session.application.port.out;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.valueobject.DeviceFingerprint;

public interface IDeviceFingerprintService {

    @NonNull
    DeviceFingerprint generate(@NonNull String ipAddress, @NonNull String userAgent);

    boolean matches(@NonNull DeviceFingerprint fingerprint1, @NonNull DeviceFingerprint fingerprint2);
}
