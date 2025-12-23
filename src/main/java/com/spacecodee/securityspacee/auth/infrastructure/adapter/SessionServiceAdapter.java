package com.spacecodee.securityspacee.auth.infrastructure.adapter;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.auth.application.port.out.AuthCreateSessionCommand;
import com.spacecodee.securityspacee.auth.application.port.out.ISessionService;
import com.spacecodee.securityspacee.session.application.port.in.ICreateSessionUseCase;

public final class SessionServiceAdapter implements ISessionService {

    private final ICreateSessionUseCase createSessionUseCase;

    public SessionServiceAdapter(@NonNull ICreateSessionUseCase createSessionUseCase) {
        this.createSessionUseCase = createSessionUseCase;
    }

    @Override
    public void createSession(@NonNull AuthCreateSessionCommand command) {
        com.spacecodee.securityspacee.session.application.command.CreateSessionCommand sessionCommand = com.spacecodee.securityspacee.session.application.command.CreateSessionCommand
                .builder()
                .userId(command.userId())
                .ipAddress(command.ipAddress())
                .userAgent(command.userAgent())
                .build();

        this.createSessionUseCase.execute(sessionCommand);
    }
}
