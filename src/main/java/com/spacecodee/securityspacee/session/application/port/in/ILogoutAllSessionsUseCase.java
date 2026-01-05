package com.spacecodee.securityspacee.session.application.port.in;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.command.LogoutAllCommand;
import com.spacecodee.securityspacee.session.application.response.LogoutAllResponse;

public interface ILogoutAllSessionsUseCase {

    @NonNull
    LogoutAllResponse execute(@NonNull LogoutAllCommand command);
}
