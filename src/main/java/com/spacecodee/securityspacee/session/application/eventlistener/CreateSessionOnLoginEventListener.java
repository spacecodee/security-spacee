package com.spacecodee.securityspacee.session.application.eventlistener;

import org.jspecify.annotations.NonNull;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import com.spacecodee.securityspacee.auth.domain.event.LoginSuccessEvent;
import com.spacecodee.securityspacee.session.application.command.CreateSessionCommand;
import com.spacecodee.securityspacee.session.application.port.in.ICreateSessionUseCase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateSessionOnLoginEventListener {

    private final ICreateSessionUseCase createSessionUseCase;

    public CreateSessionOnLoginEventListener(@NonNull ICreateSessionUseCase createSessionUseCase) {
        this.createSessionUseCase = createSessionUseCase;
    }

    @Async
    @EventListener
    public void onLoginSuccess(@NonNull LoginSuccessEvent event) {
        log.info("📝 Creating session for user ID: {} after successful login", event.getUserId());

        CreateSessionCommand command = CreateSessionCommand.builder()
                .userId(event.getUserId())
                .ipAddress(event.getIpAddress())
                .userAgent(event.getUserAgent())
                .build();

        var sessionResponse = this.createSessionUseCase.execute(command);

        log.info("✅ Session created successfully: {}", sessionResponse.sessionId());
    }
}
