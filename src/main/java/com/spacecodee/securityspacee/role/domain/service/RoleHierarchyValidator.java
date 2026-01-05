package com.spacecodee.securityspacee.role.domain.service;

import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spacecodee.securityspacee.role.domain.exception.RoleHierarchyCycleException;
import com.spacecodee.securityspacee.role.domain.repository.IRoleRepository;

public class RoleHierarchyValidator {

    private static final Logger log = LoggerFactory.getLogger(RoleHierarchyValidator.class);

    private final IRoleRepository roleRepository;

    public RoleHierarchyValidator(IRoleRepository roleRepository) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    public void validateNoCycle(@NonNull Integer roleId, @Nullable Integer newParentRoleId) {
        Objects.requireNonNull(roleId);

        if (newParentRoleId == null) {
            return;
        }

        if (roleId.equals(newParentRoleId)) {
            log.warn("Role {} cannot be its own parent", roleId);
            throw new RoleHierarchyCycleException("role.exception.role_hierarchy_cycle.self_parent");
        }

        Set<Integer> descendants = this.roleRepository.findAllDescendantIds(roleId);

        if (descendants.contains(newParentRoleId)) {
            log.warn("Role {} cannot set parent to {} - would create cycle. Descendants: {}",
                    roleId, newParentRoleId, descendants);
            throw new RoleHierarchyCycleException("role.exception.role_hierarchy_cycle.descendant_parent");
        }
    }
}
