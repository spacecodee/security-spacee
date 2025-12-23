package com.spacecodee.securityspacee.jwttoken.application.listener;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.spacecodee.securityspacee.jwttoken.application.command.RevokeAllSessionTokensCommand;
import com.spacecodee.securityspacee.jwttoken.application.event.SessionLoggedOutEvent;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRevokeAllSessionTokensUseCase;

public class RevokeTokensOnLogoutEventListener {

    private static final Logger log = LoggerFactory.getLogger(RevokeTokensOnLogoutEventListener.class);

    private final IRevokeAllSessionTokensUseCase revokeAllSessionTokensUseCase;

    public RevokeTokensOnLogoutEventListener(IRevokeAllSessionTokensUseCase revokeAllSessionTokensUseCase) {
        this.revokeAllSessionTokensUseCase = revokeAllSessionTokensUseCase;
    }

    @Async
    @EventListener
    public void handleSessionLoggedOut(@NonNull SessionLoggedOutEvent event) {
        log.info("Session logged out event received for sessionId: {}", event.getSessionId());

        RevokeAllSessionTokensCommand command = new RevokeAllSessionTokensCommand(
                event.getSessionId(),
                event.getUserId(),
                "manual_logout");

        this.revokeAllSessionTokensUseCase.execute(command);

        log.info("All tokens revoked for session: {}", event.getSessionId());
    }
}
