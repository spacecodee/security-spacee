package com.spacecodee.securityspacee.jwttoken.infrastructure.listener;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.spacecodee.securityspacee.jwttoken.application.command.RevokeAllSessionTokensCommand;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRevokeAllSessionTokensUseCase;
import com.spacecodee.securityspacee.session.domain.event.SessionLoggedOutEvent;

public class RevokeTokensOnLogoutEventListener {

    private static final Logger log = LoggerFactory.getLogger(RevokeTokensOnLogoutEventListener.class);

    private final IRevokeAllSessionTokensUseCase revokeAllSessionTokensUseCase;

    public RevokeTokensOnLogoutEventListener(@NonNull IRevokeAllSessionTokensUseCase revokeAllSessionTokensUseCase) {
        this.revokeAllSessionTokensUseCase = revokeAllSessionTokensUseCase;
    }

    @Async
    @EventListener
    public void handleSessionLoggedOutEvent(@NonNull SessionLoggedOutEvent event) {
        log.info("Processing SessionLoggedOutEvent: sessionId={}, userId={}, reason={}",
                event.getSessionId().getValue(),
                event.getUserId(),
                event.getLogoutReason());

        final RevokeAllSessionTokensCommand command = new RevokeAllSessionTokensCommand(
                event.getSessionId().getValue().toString(),
                event.getUserId(),
                event.getLogoutReason().name().toLowerCase());

        this.revokeAllSessionTokensUseCase.execute(command);

        log.info("Successfully revoked all tokens for session: {}", event.getSessionId().getValue());
    }
}
