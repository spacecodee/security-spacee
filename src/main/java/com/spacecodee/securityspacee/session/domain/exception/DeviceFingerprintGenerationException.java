package com.spacecodee.securityspacee.session.domain.exception;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.shared.exception.base.ValidationException;

public final class DeviceFingerprintGenerationException extends ValidationException {

    public DeviceFingerprintGenerationException() {
        super("session.exception.device_fingerprint_generation_failed");
    }

    public DeviceFingerprintGenerationException(@NonNull String message) {
        super(message);
    }

    public DeviceFingerprintGenerationException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
