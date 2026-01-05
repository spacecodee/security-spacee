package com.spacecodee.securityspacee.session.adapter.response;

import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Session status response with expiration information")
public final class SessionStatusResponse {

    @Schema(description = "Whether the session is expired", example = "false")
    @JsonProperty("is_expired")
    private final boolean isExpired;

    @Schema(description = "Remaining idle time in seconds", example = "1800")
    @JsonProperty("remaining_idle_time_seconds")
    private final long remainingIdleTimeSeconds;

    @Schema(description = "Remaining absolute time in seconds", example = "82800")
    @JsonProperty("remaining_absolute_time_seconds")
    private final long remainingAbsoluteTimeSeconds;

    @Schema(description = "Whether to show warning to user", example = "false")
    @JsonProperty("should_show_warning")
    private final boolean shouldShowWarning;

    @Schema(description = "Warning message to display to user", example = "Your session will expire in 5 minutes")
    @JsonProperty("warning_message")
    @Nullable
    private final String warningMessage;

    public SessionStatusResponse(
            boolean isExpired,
            long remainingIdleTimeSeconds,
            long remainingAbsoluteTimeSeconds,
            boolean shouldShowWarning,
            @Nullable String warningMessage) {
        this.isExpired = isExpired;
        this.remainingIdleTimeSeconds = remainingIdleTimeSeconds;
        this.remainingAbsoluteTimeSeconds = remainingAbsoluteTimeSeconds;
        this.shouldShowWarning = shouldShowWarning;
        this.warningMessage = warningMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SessionStatusResponse that = (SessionStatusResponse) o;
        return this.isExpired == that.isExpired &&
                this.remainingIdleTimeSeconds == that.remainingIdleTimeSeconds &&
                this.remainingAbsoluteTimeSeconds == that.remainingAbsoluteTimeSeconds &&
                this.shouldShowWarning == that.shouldShowWarning;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.isExpired, this.remainingIdleTimeSeconds, this.remainingAbsoluteTimeSeconds,
                this.shouldShowWarning);
    }

    @Override
    public @NonNull String toString() {
        return "SessionStatusResponse{" +
                "isExpired=" + this.isExpired +
                ", remainingIdleTimeSeconds=" + this.remainingIdleTimeSeconds +
                ", remainingAbsoluteTimeSeconds=" + this.remainingAbsoluteTimeSeconds +
                ", shouldShowWarning=" + this.shouldShowWarning +
                ", warningMessage='" + this.warningMessage + '\'' +
                '}';
    }
}
