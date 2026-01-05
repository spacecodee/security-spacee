package com.spacecodee.securityspacee.role.infrastructure.persistence.mapper.impl;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.role.domain.model.Role;
import com.spacecodee.securityspacee.role.domain.valueobject.Description;
import com.spacecodee.securityspacee.role.domain.valueobject.MaxUsers;
import com.spacecodee.securityspacee.role.domain.valueobject.RoleId;
import com.spacecodee.securityspacee.role.domain.valueobject.RoleMetadata;
import com.spacecodee.securityspacee.role.domain.valueobject.RoleName;
import com.spacecodee.securityspacee.role.domain.valueobject.SystemRoleTag;
import com.spacecodee.securityspacee.role.infrastructure.persistence.jpa.RoleEntity;
import com.spacecodee.securityspacee.role.infrastructure.persistence.mapper.IRolePersistenceMapper;

public class RolePersistenceMapperImpl implements IRolePersistenceMapper {

    @Override
    public @NonNull RoleEntity toEntity(@NonNull Role role) {
        return RoleEntity.builder()
                .roleId(role.getRoleId() != null ? role.getRoleId().getValue() : null)
                .roleName(role.getRoleName().getValue())
                .description(role.getDescription().getValue())
                .parentRoleId(role.getParentRoleId() != null ? role.getParentRoleId().getValue() : null)
                .hierarchyLevel(role.getHierarchyLevel())
                .systemRoleTag(role.getSystemRoleTag() != null ? role.getSystemRoleTag().name() : null)
                .maxUsers(role.getMaxUsers() != null ? role.getMaxUsers().getValue() : null)
                .currentUsers(role.getCurrentUsers())
                .isActive(role.isActive())
                .createdAt(role.getMetadata().getCreatedAt())
                .updatedAt(role.getMetadata().getUpdatedAt())
                .build();
    }

    @Override
    public @NonNull Role toDomain(@NonNull RoleEntity entity) {
        return Role.builder()
                .roleId(entity.getRoleId() != null ? RoleId.of(entity.getRoleId()) : null)
                .roleName(RoleName.of(entity.getRoleName()))
                .description(Description.of(entity.getDescription()))
                .parentRoleId(entity.getParentRoleId() != null ? RoleId.of(entity.getParentRoleId()) : null)
                .hierarchyLevel(entity.getHierarchyLevel())
                .systemRoleTag(entity.getSystemRoleTag() != null ? SystemRoleTag.of(entity.getSystemRoleTag()) : null)
                .maxUsers(entity.getMaxUsers() != null ? MaxUsers.of(entity.getMaxUsers()) : null)
                .currentUsers(entity.getCurrentUsers())
                .isActive(entity.isActive())
                .metadata(RoleMetadata.builder()
                        .createdAt(entity.getCreatedAt())
                        .updatedAt(entity.getUpdatedAt())
                        .version(1)
                        .build())
                .build();
    }
}
