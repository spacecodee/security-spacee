package com.spacecodee.securityspacee.role.application.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.spacecodee.securityspacee.role.domain.repository.IRoleRepository;

public class RoleHierarchyService {

    private static final Logger log = LoggerFactory.getLogger(RoleHierarchyService.class);

    private final IRoleRepository roleRepository;

    public RoleHierarchyService(IRoleRepository roleRepository) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Cacheable(value = "role-hierarchy", key = "#roleIds")
    public @NonNull Set<Integer> getAllRolesWithInheritance(@NonNull List<Integer> roleIds) {
        Objects.requireNonNull(roleIds);
        log.debug("Resolving role hierarchy for: {}", roleIds);

        Set<Integer> effectiveRoles = this.roleRepository.findRoleIdsWithInheritance(roleIds);

        log.debug("Effective roles with inheritance: {}", effectiveRoles);
        return effectiveRoles;
    }

    @CacheEvict(value = "role-hierarchy", allEntries = true)
    public void invalidateHierarchyCache() {
        log.info("Role hierarchy cache invalidated");
    }
}
