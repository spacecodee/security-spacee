package com.spacecodee.securityspacee.role.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.role.domain.model.Role;
import com.spacecodee.securityspacee.role.domain.valueobject.RoleId;
import com.spacecodee.securityspacee.role.domain.valueobject.RoleName;
import com.spacecodee.securityspacee.role.domain.valueobject.SystemRoleTag;

public interface IRoleRepository {

    @NonNull
    Role save(@NonNull Role role);

    @NonNull
    Optional<Role> findById(@NonNull RoleId roleId);

    @NonNull
    List<Role> findAll();

    void delete(@NonNull Role role);

    boolean existsByName(@NonNull RoleName roleName);

    @NonNull
    Optional<Role> findByName(@NonNull RoleName roleName);

    boolean existsBySystemRoleTag(@NonNull SystemRoleTag systemRoleTag);

    @NonNull
    Optional<Role> findBySystemRoleTag(@NonNull SystemRoleTag systemRoleTag);

    @NonNull
    List<Role> findByParentRoleId(@NonNull RoleId parentRoleId);

    @NonNull
    List<Role> findRootRoles();

    @NonNull
    List<Role> findRolesWithAvailableSlots();

    @NonNull
    List<Role> findActiveRoles();

    @NonNull
    List<Role> findInactiveRoles();

    @NonNull
    Set<Integer> findRoleIdsWithInheritance(@NonNull List<Integer> roleIds);

    @NonNull
    Set<Integer> findAllDescendantIds(@NonNull Integer roleId);
}
