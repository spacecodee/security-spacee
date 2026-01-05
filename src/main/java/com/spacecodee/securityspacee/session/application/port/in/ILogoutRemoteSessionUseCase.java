package com.spacecodee.securityspacee.session.application.port.in;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.command.LogoutRemoteSessionCommand;
import com.spacecodee.securityspacee.session.application.response.LogoutResponse;

public interface ILogoutRemoteSessionUseCase {

    @NonNull
    LogoutResponse execute(@NonNull LogoutRemoteSessionCommand command);
}
