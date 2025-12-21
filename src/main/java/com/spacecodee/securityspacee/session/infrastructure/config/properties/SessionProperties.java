package com.spacecodee.securityspacee.session.infrastructure.config.properties;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "session")
public record SessionProperties(Duration expiration, Concurrent concurrent) {

    private static final String DEFAULT_FINGERPRINT_ALGORITHM = "SHA-256";
    private static final String DEFAULT_STRATEGY = "FIFO";

    public SessionProperties {
        if (expiration == null || expiration.isNegative() || expiration.isZero()) {
            expiration = Duration.ofHours(24);
        }
        if (concurrent == null) {
            concurrent = new Concurrent(true, 5, DEFAULT_STRATEGY, true, new GeoIp(false, null),
                    new DeviceFingerprint(DEFAULT_FINGERPRINT_ALGORITHM));
        }
    }

    public record Concurrent(
            boolean enabled,
            int defaultMax,
            String defaultStrategy,
            boolean notifyNewDevice,
            GeoIp geoIp,
            DeviceFingerprint deviceFingerprint) {

        private static final String DEFAULT_FINGERPRINT_ALGORITHM = "SHA-256";
        private static final String DEFAULT_STRATEGY = "FIFO";

        public Concurrent {
            if (defaultMax <= 0) {
                defaultMax = 5;
            }
            if (defaultStrategy == null || defaultStrategy.isBlank()) {
                defaultStrategy = DEFAULT_STRATEGY;
            }
            if (geoIp == null) {
                geoIp = new GeoIp(false, null);
            }
            if (deviceFingerprint == null) {
                deviceFingerprint = new DeviceFingerprint(DEFAULT_FINGERPRINT_ALGORITHM);
            }
        }
    }

    public record GeoIp(boolean enabled, String databasePath) {
    }

    public record DeviceFingerprint(String algorithm) {

        private static final String DEFAULT_ALGORITHM = "SHA-256";

        public DeviceFingerprint {
            if (algorithm == null || algorithm.isBlank()) {
                algorithm = DEFAULT_ALGORITHM;
            }
        }
    }
}
