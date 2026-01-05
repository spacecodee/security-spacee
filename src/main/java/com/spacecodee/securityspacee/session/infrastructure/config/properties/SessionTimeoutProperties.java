package com.spacecodee.securityspacee.session.infrastructure.config.properties;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "session.timeout")
public final class SessionTimeoutProperties {

    private Duration absolute = Duration.ofHours(24);
    private Duration idle = Duration.ofMinutes(30);
    private boolean slidingExpiration = false;
    private Duration warningThreshold = Duration.ofMinutes(5);

    private CleanupProperties cleanup = new CleanupProperties();
    private ActivityTrackingProperties activityTracking = new ActivityTrackingProperties();

    @Getter
    @Setter
    public static final class CleanupProperties {
        private long idleCheckInterval = 60000L;
        private long absoluteCheckInterval = 300000L;
    }

    @Getter
    @Setter
    public static final class ActivityTrackingProperties {
        private boolean enabled = true;
        private boolean async = true;
        private boolean batchUpdates = false;
    }
}
