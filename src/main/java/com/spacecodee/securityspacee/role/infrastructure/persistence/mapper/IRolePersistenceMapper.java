package com.spacecodee.securityspacee.role.infrastructure.persistence.mapper;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.role.domain.model.Role;
import com.spacecodee.securityspacee.role.infrastructure.persistence.jpa.RoleEntity;

public interface IRolePersistenceMapper {

    @NonNull
    RoleEntity toEntity(@NonNull Role role);

    @NonNull
    Role toDomain(@NonNull RoleEntity entity);
}
