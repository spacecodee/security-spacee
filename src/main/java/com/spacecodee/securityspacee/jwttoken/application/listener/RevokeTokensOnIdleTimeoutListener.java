package com.spacecodee.securityspacee.jwttoken.application.listener;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.spacecodee.securityspacee.jwttoken.application.command.RevokeAllSessionTokensCommand;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRevokeAllSessionTokensUseCase;
import com.spacecodee.securityspacee.session.domain.event.SessionExpiredByIdleEvent;

public class RevokeTokensOnIdleTimeoutListener {

    private static final Logger log = LoggerFactory.getLogger(RevokeTokensOnIdleTimeoutListener.class);

    private final IRevokeAllSessionTokensUseCase revokeAllSessionTokensUseCase;

    public RevokeTokensOnIdleTimeoutListener(
            @NonNull IRevokeAllSessionTokensUseCase revokeAllSessionTokensUseCase) {
        this.revokeAllSessionTokensUseCase = revokeAllSessionTokensUseCase;
    }

    @Async
    @EventListener
    public void handleSessionExpiredByIdle(@NonNull SessionExpiredByIdleEvent event) {
        log.info("Session expired by idle timeout event received for sessionId: {}", event.getSessionId());

        RevokeAllSessionTokensCommand command = new RevokeAllSessionTokensCommand(
                event.getSessionId(),
                event.getUserId(),
                "session_idle_timeout");

        this.revokeAllSessionTokensUseCase.execute(command);

        log.info("All tokens revoked for idle timeout session: {}", event.getSessionId());
    }
}
