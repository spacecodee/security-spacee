package com.spacecodee.securityspacee.role.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringJpaRoleRepository extends JpaRepository<RoleEntity, Integer> {

    boolean existsByRoleName(@NonNull String roleName);

    @NonNull
    Optional<RoleEntity> findByRoleName(@NonNull String roleName);

    boolean existsBySystemRoleTag(@NonNull String systemRoleTag);

    @NonNull
    Optional<RoleEntity> findBySystemRoleTag(@NonNull String systemRoleTag);

    @NonNull
    List<RoleEntity> findByParentRoleId(@NonNull Integer parentRoleId);

    @NonNull
    @Query("SELECT r FROM RoleEntity r WHERE r.parentRoleId IS NULL")
    List<RoleEntity> findRootRoles();

    @NonNull
    @Query("SELECT r FROM RoleEntity r WHERE r.maxUsers IS NULL OR r.currentUsers < r.maxUsers")
    List<RoleEntity> findRolesWithAvailableSlots();

    @NonNull
    List<RoleEntity> findByIsActive(boolean isActive);

    @NonNull
    @Query(value = """
            WITH RECURSIVE role_hierarchy AS (
                SELECT role_id, parent_role_id, hierarchy_level
                FROM role
                WHERE role_id IN :roleIds
            
                UNION
            
                SELECT r.role_id, r.parent_role_id, r.hierarchy_level
                FROM role r
                INNER JOIN role_hierarchy rh ON r.role_id = rh.parent_role_id
            )
            SELECT DISTINCT role_id FROM role_hierarchy
            ORDER BY hierarchy_level
            """, nativeQuery = true)
    List<Integer> findRoleIdsWithInheritance(@Param("roleIds") @NonNull List<Integer> roleIds);

    @NonNull
    @Query(value = """
            WITH RECURSIVE role_descendants AS (
                SELECT role_id
                FROM role
                WHERE role_id = :roleId
            
                UNION
            
                SELECT r.role_id
                FROM role r
                INNER JOIN role_descendants rd ON r.parent_role_id = rd.role_id
            )
            SELECT DISTINCT role_id FROM role_descendants
            WHERE role_id != :roleId
            """, nativeQuery = true)
    List<Integer> findAllDescendantIds(@Param("roleId") @NonNull Integer roleId);
}
