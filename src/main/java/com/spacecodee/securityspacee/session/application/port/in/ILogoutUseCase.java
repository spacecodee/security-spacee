package com.spacecodee.securityspacee.session.application.port.in;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.command.LogoutCommand;
import com.spacecodee.securityspacee.session.application.response.LogoutResponse;

public interface ILogoutUseCase {

    @NonNull
    LogoutResponse execute(@NonNull LogoutCommand command);
}
