package com.spacecodee.securityspacee.auth.application.port.in;

import com.spacecodee.securityspacee.auth.application.command.LoginCommand;
import com.spacecodee.securityspacee.auth.application.response.AuthenticationResponse;

public interface ILoginUseCase {

    AuthenticationResponse execute(LoginCommand command);
}
