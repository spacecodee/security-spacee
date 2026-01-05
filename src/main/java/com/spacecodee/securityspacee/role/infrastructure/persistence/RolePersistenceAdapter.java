package com.spacecodee.securityspacee.role.infrastructure.persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spacecodee.securityspacee.role.domain.model.Role;
import com.spacecodee.securityspacee.role.domain.repository.IRoleRepository;
import com.spacecodee.securityspacee.role.domain.valueobject.RoleId;
import com.spacecodee.securityspacee.role.domain.valueobject.RoleName;
import com.spacecodee.securityspacee.role.domain.valueobject.SystemRoleTag;
import com.spacecodee.securityspacee.role.infrastructure.persistence.jpa.RoleEntity;
import com.spacecodee.securityspacee.role.infrastructure.persistence.jpa.SpringJpaRoleRepository;
import com.spacecodee.securityspacee.role.infrastructure.persistence.mapper.IRolePersistenceMapper;

public class RolePersistenceAdapter implements IRoleRepository {

    private static final Logger log = LoggerFactory.getLogger(RolePersistenceAdapter.class);

    private final SpringJpaRoleRepository jpaRepository;
    private final IRolePersistenceMapper mapper;

    public RolePersistenceAdapter(SpringJpaRoleRepository jpaRepository, IRolePersistenceMapper mapper) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public @NonNull Role save(@NonNull Role role) {
        Objects.requireNonNull(role);
        log.debug("Saving role: {}", role.getRoleName().getValue());

        RoleEntity entity = this.mapper.toEntity(role);
        RoleEntity savedEntity = this.jpaRepository.save(entity);

        log.debug("Role saved with ID: {}", savedEntity.getRoleId());
        return this.mapper.toDomain(savedEntity);
    }

    @Override
    public @NonNull Optional<Role> findById(@NonNull RoleId roleId) {
        Objects.requireNonNull(roleId);
        log.debug("Finding role by ID: {}", roleId.getValue());

        return this.jpaRepository.findById(roleId.getValue())
                .map(this.mapper::toDomain);
    }

    @Override
    public @NonNull List<Role> findAll() {
        log.debug("Finding all roles");
        return this.jpaRepository.findAll().stream()
                .map(this.mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(@NonNull Role role) {
        Objects.requireNonNull(role);
        log.debug("Deleting role: {}", role.getRoleName().getValue());

        RoleEntity entity = this.mapper.toEntity(role);
        this.jpaRepository.delete(entity);
    }

    @Override
    public boolean existsByName(@NonNull RoleName roleName) {
        Objects.requireNonNull(roleName);
        log.debug("Checking if role exists by name: {}", roleName.getValue());

        return this.jpaRepository.existsByRoleName(roleName.getValue());
    }

    @Override
    public @NonNull Optional<Role> findByName(@NonNull RoleName roleName) {
        Objects.requireNonNull(roleName);
        log.debug("Finding role by name: {}", roleName.getValue());

        return this.jpaRepository.findByRoleName(roleName.getValue())
                .map(this.mapper::toDomain);
    }

    @Override
    public boolean existsBySystemRoleTag(@NonNull SystemRoleTag systemRoleTag) {
        Objects.requireNonNull(systemRoleTag);
        log.debug("Checking if role exists by system tag: {}", systemRoleTag.name());

        return this.jpaRepository.existsBySystemRoleTag(systemRoleTag.name());
    }

    @Override
    public @NonNull Optional<Role> findBySystemRoleTag(@NonNull SystemRoleTag systemRoleTag) {
        Objects.requireNonNull(systemRoleTag);
        log.debug("Finding role by system tag: {}", systemRoleTag.name());

        return this.jpaRepository.findBySystemRoleTag(systemRoleTag.name())
                .map(this.mapper::toDomain);
    }

    @Override
    public @NonNull List<Role> findByParentRoleId(@NonNull RoleId parentRoleId) {
        Objects.requireNonNull(parentRoleId);
        log.debug("Finding roles by parent ID: {}", parentRoleId.getValue());

        return this.jpaRepository.findByParentRoleId(parentRoleId.getValue()).stream()
                .map(this.mapper::toDomain)
                .toList();
    }

    @Override
    public @NonNull List<Role> findRootRoles() {
        log.debug("Finding root roles");
        return this.jpaRepository.findRootRoles().stream()
                .map(this.mapper::toDomain)
                .toList();
    }

    @Override
    public @NonNull List<Role> findRolesWithAvailableSlots() {
        log.debug("Finding roles with available slots");
        return this.jpaRepository.findRolesWithAvailableSlots().stream()
                .map(this.mapper::toDomain)
                .toList();
    }

    @Override
    public @NonNull List<Role> findActiveRoles() {
        log.debug("Finding active roles");
        return this.jpaRepository.findByIsActive(true).stream()
                .map(this.mapper::toDomain)
                .toList();
    }

    @Override
    public @NonNull List<Role> findInactiveRoles() {
        log.debug("Finding inactive roles");
        return this.jpaRepository.findByIsActive(false).stream()
                .map(this.mapper::toDomain)
                .toList();
    }

    @Override
    public @NonNull Set<Integer> findRoleIdsWithInheritance(@NonNull List<Integer> roleIds) {
        Objects.requireNonNull(roleIds);
        log.debug("Finding role IDs with inheritance for: {}", roleIds);

        List<Integer> result = this.jpaRepository.findRoleIdsWithInheritance(roleIds);
        return new HashSet<>(result);
    }

    @Override
    public @NonNull Set<Integer> findAllDescendantIds(@NonNull Integer roleId) {
        Objects.requireNonNull(roleId);
        log.debug("Finding all descendant IDs for role: {}", roleId);

        List<Integer> result = this.jpaRepository.findAllDescendantIds(roleId);
        return new HashSet<>(result);
    }
}
