package com.spacecodee.securityspacee.role.application.mapper;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.spacecodee.securityspacee.role.application.response.RoleResponse;
import com.spacecodee.securityspacee.role.domain.model.Role;

public interface IRoleResponseMapper {

    @NonNull
    RoleResponse toResponse(@NonNull Role role, @Nullable String parentRoleName, @NonNull String createdByUsername);
}
