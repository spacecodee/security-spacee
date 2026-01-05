package com.spacecodee.securityspacee.role.application.port.in;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.role.application.command.CreateRoleCommand;
import com.spacecodee.securityspacee.role.application.response.RoleResponse;

public interface ICreateRoleUseCase {

    @NonNull
    RoleResponse execute(@NonNull CreateRoleCommand command);
}
