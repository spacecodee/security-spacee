package com.spacecodee.securityspacee.session.application.port.in;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.command.LogoutSessionCommand;

public interface ILogoutSessionUseCase {

    void execute(@NonNull LogoutSessionCommand command);
}
