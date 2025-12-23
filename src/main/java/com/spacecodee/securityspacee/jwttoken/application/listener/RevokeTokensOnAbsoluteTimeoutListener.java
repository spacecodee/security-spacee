package com.spacecodee.securityspacee.jwttoken.application.listener;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.spacecodee.securityspacee.jwttoken.application.command.RevokeAllSessionTokensCommand;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRevokeAllSessionTokensUseCase;
import com.spacecodee.securityspacee.session.domain.event.SessionExpiredByAbsoluteTimeoutEvent;

public class RevokeTokensOnAbsoluteTimeoutListener {

    private static final Logger log = LoggerFactory.getLogger(RevokeTokensOnAbsoluteTimeoutListener.class);

    private final IRevokeAllSessionTokensUseCase revokeAllSessionTokensUseCase;

    public RevokeTokensOnAbsoluteTimeoutListener(
            @NonNull IRevokeAllSessionTokensUseCase revokeAllSessionTokensUseCase) {
        this.revokeAllSessionTokensUseCase = revokeAllSessionTokensUseCase;
    }

    @Async
    @EventListener
    public void handleSessionExpiredByAbsoluteTimeout(@NonNull SessionExpiredByAbsoluteTimeoutEvent event) {
        log.info("Session expired by absolute timeout event received for sessionId: {}", event.getSessionId());

        RevokeAllSessionTokensCommand command = new RevokeAllSessionTokensCommand(
                event.getSessionId(),
                event.getUserId(),
                "session_absolute_timeout");

        this.revokeAllSessionTokensUseCase.execute(command);

        log.info("All tokens revoked for absolute timeout session: {}", event.getSessionId());
    }
}
