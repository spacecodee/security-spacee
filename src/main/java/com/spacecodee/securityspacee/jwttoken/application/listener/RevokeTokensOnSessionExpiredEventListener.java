package com.spacecodee.securityspacee.jwttoken.application.listener;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.spacecodee.securityspacee.jwttoken.application.command.RevokeAllSessionTokensCommand;
import com.spacecodee.securityspacee.jwttoken.application.event.SessionExpiredEvent;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRevokeAllSessionTokensUseCase;

public class RevokeTokensOnSessionExpiredEventListener {

    private static final Logger log = LoggerFactory.getLogger(RevokeTokensOnSessionExpiredEventListener.class);

    private final IRevokeAllSessionTokensUseCase revokeAllSessionTokensUseCase;

    public RevokeTokensOnSessionExpiredEventListener(IRevokeAllSessionTokensUseCase revokeAllSessionTokensUseCase) {
        this.revokeAllSessionTokensUseCase = revokeAllSessionTokensUseCase;
    }

    @Async
    @EventListener
    public void handleSessionExpired(@NonNull SessionExpiredEvent event) {
        log.info("Session expired event received for sessionId: {}", event.getSessionId());

        RevokeAllSessionTokensCommand command = new RevokeAllSessionTokensCommand(
                event.getSessionId(),
                event.getUserId(),
                "session_expired");

        this.revokeAllSessionTokensUseCase.execute(command);

        log.info("All tokens revoked for expired session: {}", event.getSessionId());
    }
}
