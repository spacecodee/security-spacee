package com.spacecodee.securityspacee.user.application.port.in;

import com.spacecodee.securityspacee.user.application.command.RegisterUserCommand;
import com.spacecodee.securityspacee.user.application.response.UserResponse;

public interface IRegisterUserUseCase {

    UserResponse register(RegisterUserCommand command);
}
