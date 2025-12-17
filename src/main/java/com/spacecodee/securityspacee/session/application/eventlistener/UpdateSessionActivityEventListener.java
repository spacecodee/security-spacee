package com.spacecodee.securityspacee.session.application.eventlistener;

import org.jspecify.annotations.NonNull;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.spacecodee.securityspacee.jwttoken.domain.event.TokenRefreshedEvent;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpdateSessionActivityEventListener {

    private final ISessionRepository sessionRepository;

    public UpdateSessionActivityEventListener(@NonNull ISessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Async
    @EventListener
    public void onTokenRefreshed(@NonNull TokenRefreshedEvent event) {
        log.info("🔄 Updating session activity for session ID: {} after token refresh", event.getSessionId());

        SessionId sessionId = SessionId.parse(event.getSessionId());

        this.sessionRepository.findById(sessionId)
                .ifPresentOrElse(
                        session -> {
                            Session updatedSession = session.updateActivity();
                            this.sessionRepository.save(updatedSession);
                            log.info("✅ Session activity updated successfully for session: {}", event.getSessionId());
                        },
                        () -> log.warn("⚠️ Session not found for ID: {} during token refresh", event.getSessionId()));
    }
}
