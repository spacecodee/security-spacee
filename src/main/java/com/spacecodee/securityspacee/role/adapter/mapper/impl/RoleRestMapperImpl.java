package com.spacecodee.securityspacee.role.adapter.mapper.impl;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.role.adapter.mapper.IRoleRestMapper;
import com.spacecodee.securityspacee.role.adapter.request.CreateRoleRequest;
import com.spacecodee.securityspacee.role.application.command.CreateRoleCommand;

public final class RoleRestMapperImpl implements IRoleRestMapper {

    @Override
    public @NonNull CreateRoleCommand toCommand(@NonNull CreateRoleRequest request) {
        return new CreateRoleCommand(
                request.name(),
                request.description(),
                request.parentRoleId(),
                request.systemRoleTag(),
                request.maxUsers(),
                request.isActive());
    }
}
