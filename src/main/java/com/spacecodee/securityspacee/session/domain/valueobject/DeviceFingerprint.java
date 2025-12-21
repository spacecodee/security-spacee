package com.spacecodee.securityspacee.session.domain.valueobject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.exception.DeviceFingerprintGenerationException;

public record DeviceFingerprint(@NonNull String value) {

    public static @NonNull DeviceFingerprint from(@NonNull String ipAddress, @NonNull String userAgent) {
        final String input = ipAddress + "|" + userAgent;
        final String hash = sha256Hex(input);
        return new DeviceFingerprint(hash.substring(0, 32));
    }

    public DeviceFingerprint {
        if (value.isBlank()) {
            throw new DeviceFingerprintGenerationException("session.validation.device_fingerprint.blank");
        }
    }

    private static @NonNull String sha256Hex(@NonNull String input) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                final String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new DeviceFingerprintGenerationException("session.exception.device_fingerprint_generation_failed", e);
        }
    }
}
