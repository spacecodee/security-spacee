package com.spacecodee.securityspacee.role.domain.model;

import java.time.Instant;
import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.spacecodee.securityspacee.role.domain.valueobject.Description;
import com.spacecodee.securityspacee.role.domain.valueobject.MaxUsers;
import com.spacecodee.securityspacee.role.domain.valueobject.RoleId;
import com.spacecodee.securityspacee.role.domain.valueobject.RoleMetadata;
import com.spacecodee.securityspacee.role.domain.valueobject.RoleName;
import com.spacecodee.securityspacee.role.domain.valueobject.SystemRoleTag;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public final class Role {

    private static final int MAX_HIERARCHY_DEPTH = 5;

    private final RoleId roleId;
    private final RoleName roleName;
    private final Description description;
    private final RoleId parentRoleId;
    private final Integer hierarchyLevel;
    private final SystemRoleTag systemRoleTag;
    private final MaxUsers maxUsers;
    private final Integer currentUsers;
    private final boolean isActive;
    private final RoleMetadata metadata;

    @SuppressWarnings("java:S107")
    Role(
            RoleId roleId,
            RoleName roleName,
            Description description,
            RoleId parentRoleId,
            Integer hierarchyLevel,
            SystemRoleTag systemRoleTag,
            MaxUsers maxUsers,
            Integer currentUsers,
            boolean isActive,
            RoleMetadata metadata) {
        this.roleId = roleId;
        this.roleName = Objects.requireNonNull(roleName, "role.validation.role_name.required");
        this.description = Objects.requireNonNull(description, "role.validation.description.required");
        this.parentRoleId = parentRoleId;
        this.hierarchyLevel = Objects.requireNonNull(hierarchyLevel, "role.validation.hierarchy_level.required");
        this.systemRoleTag = systemRoleTag;
        this.maxUsers = maxUsers;
        this.currentUsers = Objects.requireNonNull(currentUsers,
                "role.validation.current_users.required");
        this.isActive = isActive;
        this.metadata = Objects.requireNonNull(metadata, "role.validation.metadata.required");

        this.validate();
    }

    @Contract("_, _, _, _, _, _ -> new")
    public static @NonNull Role create(
            RoleName roleName,
            Description description,
            @Nullable RoleId parentRoleId,
            Integer hierarchyLevel,
            @Nullable SystemRoleTag systemRoleTag,
            @Nullable MaxUsers maxUsers) {
        return Role.builder()
                .roleId(null)
                .roleName(roleName)
                .description(description)
                .parentRoleId(parentRoleId)
                .hierarchyLevel(hierarchyLevel)
                .systemRoleTag(systemRoleTag)
                .maxUsers(maxUsers)
                .currentUsers(0)
                .isActive(true)
                .metadata(RoleMetadata.forCreation(Instant.now()))
                .build();
    }

    private void validate() {
        if (this.hierarchyLevel < 0) {
            throw new IllegalStateException("role.validation.hierarchy_level.negative");
        }

        if (this.hierarchyLevel > MAX_HIERARCHY_DEPTH) {
            throw new IllegalStateException("role.validation.hierarchy_level.max_depth");
        }

        if (this.currentUsers < 0) {
            throw new IllegalStateException("role.validation.current_users.negative");
        }
    }

    public boolean isSystemRole() {
        return this.systemRoleTag != null;
    }

    public boolean canBeDeleted() {
        return !this.isSystemRole() && this.currentUsers == 0;
    }

    public boolean hasReachedMaxUsers() {
        if (this.maxUsers == null) {
            return false;
        }
        return this.currentUsers >= this.maxUsers.getValue();
    }

    @Contract(" -> new")
    public @NonNull Role incrementUserCount() {
        int newCount = this.currentUsers + 1;

        if (this.maxUsers != null && newCount > this.maxUsers.getValue()) {
            throw new IllegalStateException("role.exception.max_cardinality_exceeded");
        }

        return this.toBuilder()
                .currentUsers(newCount)
                .build();
    }

    @Contract(" -> new")
    public @NonNull Role decrementUserCount() {
        if (this.currentUsers > 0) {
            return this.toBuilder()
                    .currentUsers(this.currentUsers - 1)
                    .build();
        }
        return this;
    }

    @Contract(" -> new")
    public @NonNull Role activate() {
        if (this.isActive) {
            throw new IllegalStateException("role.exception.already_active");
        }

        return this.toBuilder()
                .isActive(true)
                .build();
    }

    @Contract(" -> new")
    public @NonNull Role deactivate() {
        if (!this.isActive) {
            throw new IllegalStateException("role.exception.already_inactive");
        }

        if (this.isSystemRole()) {
            throw new IllegalStateException("role.exception.cannot_deactivate_system_role");
        }

        return this.toBuilder()
                .isActive(false)
                .build();
    }

    @Contract("_ -> new")
    public @NonNull Role updateDescription(Description newDescription) {
        return this.toBuilder()
                .description(newDescription)
                .metadata(this.metadata.forUpdate(Instant.now()))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Role role = (Role) o;
        return Objects.equals(this.roleId, role.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.roleId);
    }
}
