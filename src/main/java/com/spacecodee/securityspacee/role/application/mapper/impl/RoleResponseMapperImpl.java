package com.spacecodee.securityspacee.role.application.mapper.impl;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.spacecodee.securityspacee.role.application.mapper.IRoleResponseMapper;
import com.spacecodee.securityspacee.role.application.response.RoleResponse;
import com.spacecodee.securityspacee.role.domain.model.Role;

public class RoleResponseMapperImpl implements IRoleResponseMapper {

    @Override
    public @NonNull RoleResponse toResponse(
            @NonNull Role role,
            @Nullable String parentRoleName,
            @NonNull String createdByUsername) {
        return new RoleResponse(
                role.getRoleId().getValue(),
                role.getRoleName().getValue(),
                role.getDescription().getValue(),
                role.getHierarchyLevel(),
                parentRoleName,
                role.getSystemRoleTag() != null ? role.getSystemRoleTag().name() : null,
                role.getMaxUsers() != null ? role.getMaxUsers().getValue() : null,
                role.getCurrentUsers(),
                role.isActive(),
                role.getMetadata().getCreatedAt(),
                createdByUsername);
    }
}
