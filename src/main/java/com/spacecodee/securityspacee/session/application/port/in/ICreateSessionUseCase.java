package com.spacecodee.securityspacee.session.application.port.in;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.command.CreateSessionCommand;
import com.spacecodee.securityspacee.session.application.response.SessionResponse;

public interface ICreateSessionUseCase {

    @NonNull
    SessionResponse execute(@NonNull CreateSessionCommand command);
}
