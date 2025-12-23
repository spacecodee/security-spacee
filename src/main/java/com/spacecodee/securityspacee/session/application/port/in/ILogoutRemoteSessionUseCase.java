package com.spacecodee.securityspacee.session.application.port.in;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.command.LogoutRemoteSessionCommand;

public interface ILogoutRemoteSessionUseCase {

    void execute(@NonNull LogoutRemoteSessionCommand command);
}
