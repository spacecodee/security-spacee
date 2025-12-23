package com.spacecodee.securityspacee.session.domain.valueobject;

import java.time.Instant;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public final class SessionMetadata {

    private final String ipAddress;
    private final String userAgent;
    private final Instant createdAt;
    private final Instant expiresAt;
    private final Instant lastActivityAt;
    @Nullable
    private final DeviceFingerprint deviceFingerprint;
    @Nullable
    private final String deviceName;
    @Nullable
    private final Location location;
    private final boolean isTrustedDevice;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SessionMetadata that = (SessionMetadata) o;
        return Objects.equals(this.ipAddress, that.ipAddress) &&
                Objects.equals(this.createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.ipAddress, this.createdAt);
    }

    @Override
    public @NonNull String toString() {
        return "SessionMetadata{" +
                "ipAddress='" + this.ipAddress + '\'' +
                ", userAgent='" + this.userAgent + '\'' +
                ", createdAt=" + this.createdAt +
                ", expiresAt=" + this.expiresAt +
                ", deviceName='" + this.deviceName + '\'' +
                ", isTrustedDevice=" + this.isTrustedDevice +
                '}';
    }
}
