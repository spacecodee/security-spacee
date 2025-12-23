package com.spacecodee.securityspacee.session.adapter.response;

import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Heartbeat response indicating session is alive")
public final class HeartbeatResponse {

    @Schema(description = "Status of the session", example = "alive")
    @JsonProperty("status")
    private final String status;

    @Schema(description = "Remaining idle time in seconds", example = "1800")
    @JsonProperty("expires_in_seconds")
    private final long expiresInSeconds;

    @Schema(description = "Remaining absolute expiration time in seconds", example = "82800")
    @JsonProperty("absolute_expires_in_seconds")
    private final long absoluteExpiresInSeconds;

    @Schema(description = "Message", example = "Session is active")
    @JsonProperty("message")
    private final String message;

    public HeartbeatResponse(
            @NonNull String status,
            long expiresInSeconds,
            long absoluteExpiresInSeconds,
            @NonNull String message) {
        this.status = Objects.requireNonNull(status, "status cannot be null");
        this.expiresInSeconds = expiresInSeconds;
        this.absoluteExpiresInSeconds = absoluteExpiresInSeconds;
        this.message = Objects.requireNonNull(message, "message cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HeartbeatResponse that = (HeartbeatResponse) o;
        return this.expiresInSeconds == that.expiresInSeconds &&
                this.absoluteExpiresInSeconds == that.absoluteExpiresInSeconds &&
                Objects.equals(this.status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.status, this.expiresInSeconds, this.absoluteExpiresInSeconds);
    }

    @Override
    public @NonNull String toString() {
        return "HeartbeatResponse{" +
                "status='" + this.status + '\'' +
                ", expiresInSeconds=" + this.expiresInSeconds +
                ", absoluteExpiresInSeconds=" + this.absoluteExpiresInSeconds +
                ", message='" + this.message + '\'' +
                '}';
    }
}
