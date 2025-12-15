package com.spacecodee.securityspacee.user.adapter.mapper;

import com.spacecodee.securityspacee.user.adapter.request.RegisterUserRequest;
import com.spacecodee.securityspacee.user.application.command.RegisterUserCommand;

public interface IUserRestMapper {

    RegisterUserCommand toCommand(RegisterUserRequest request);
}
