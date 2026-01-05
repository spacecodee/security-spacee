package com.spacecodee.securityspacee.jwttoken.application.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.spacecodee.securityspacee.auth.domain.event.LoginSuccessEvent;
import com.spacecodee.securityspacee.jwttoken.application.command.IssueTokenCommand;
import com.spacecodee.securityspacee.jwttoken.application.port.in.IIssueTokenUseCase;

@Component
public final class IssueTokensOnLoginEventListener {

    private final IIssueTokenUseCase issueTokenUseCase;

    public IssueTokensOnLoginEventListener(IIssueTokenUseCase issueTokenUseCase) {
        this.issueTokenUseCase = issueTokenUseCase;
    }

    @EventListener
    public void handleLoginSuccessEvent(LoginSuccessEvent event) {
        IssueTokenCommand command = new IssueTokenCommand(
                event.getUserId(),
                event.getUsername(),
                "session-placeholder",
                event.getAssignedRoles(),
                event.getIpAddress(),
                event.getUserAgent());

        this.issueTokenUseCase.execute(command);
    }
}
