package com.spacecodee.securityspacee.session.infrastructure.event;

import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;

import com.spacecodee.securityspacee.session.domain.event.SessionLoggedOutEvent;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.valueobject.LogoutInfo;

public class SessionEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public SessionEventPublisher(@NonNull ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public @NonNull ApplicationEventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    public void publishSessionLoggedOutEvent(@NonNull Session session, boolean wasRemoteLogout) {
        final LogoutInfo logoutInfo = session.getLogoutInfo();
        Objects.requireNonNull(logoutInfo, "logoutInfo cannot be null after logout");

        final SessionLoggedOutEvent event = SessionLoggedOutEvent.builder()
                .sessionId(session.getSessionId())
                .userId(session.getUserId())
                .logoutReason(logoutInfo.getLogoutReason())
                .loggedOutAt(logoutInfo.getLogoutAt())
                .loggedOutBy(logoutInfo.getLoggedOutBy())
                .deviceName(session.getMetadata().getDeviceName())
                .wasRemoteLogout(wasRemoteLogout)
                .build();

        this.eventPublisher.publishEvent(event);
    }
}
