package com.spacecodee.securityspacee.role.adapter.mapper;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.role.adapter.request.CreateRoleRequest;
import com.spacecodee.securityspacee.role.application.command.CreateRoleCommand;

public interface IRoleRestMapper {

    @NonNull
    CreateRoleCommand toCommand(@NonNull CreateRoleRequest request);
}
