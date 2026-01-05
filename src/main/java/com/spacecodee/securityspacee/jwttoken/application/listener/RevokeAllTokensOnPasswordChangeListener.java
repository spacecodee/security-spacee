package com.spacecodee.securityspacee.jwttoken.application.listener;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.spacecodee.securityspacee.jwttoken.application.command.RevokeAllUserTokensCommand;
import com.spacecodee.securityspacee.jwttoken.application.event.UserPasswordChangedEvent;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IRevokeAllUserTokensUseCase;

public class RevokeAllTokensOnPasswordChangeListener {

    private static final Logger log = LoggerFactory.getLogger(RevokeAllTokensOnPasswordChangeListener.class);

    private final IRevokeAllUserTokensUseCase revokeAllUserTokensUseCase;

    public RevokeAllTokensOnPasswordChangeListener(IRevokeAllUserTokensUseCase revokeAllUserTokensUseCase) {
        this.revokeAllUserTokensUseCase = revokeAllUserTokensUseCase;
    }

    @Async
    @EventListener
    public void handleUserPasswordChanged(@NonNull UserPasswordChangedEvent event) {
        log.info("User password changed event received for userId: {}", event.getUserId());

        RevokeAllUserTokensCommand command = new RevokeAllUserTokensCommand(
                event.getUserId(),
                event.getChangedBy(),
                "password_changed");

        this.revokeAllUserTokensUseCase.execute(command);

        log.info("All tokens revoked for user: {}", event.getUserId());
    }
}
