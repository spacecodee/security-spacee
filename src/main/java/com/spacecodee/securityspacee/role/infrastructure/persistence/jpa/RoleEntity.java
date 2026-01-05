package com.spacecodee.securityspacee.role.infrastructure.persistence.jpa;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    @Column(name = "description")
    private String description;

    @Column(name = "parent_role_id")
    private Integer parentRoleId;

    @Column(name = "hierarchy_level", nullable = false)
    private Integer hierarchyLevel;

    @Column(name = "system_role_tag", unique = true, length = 50)
    private String systemRoleTag;

    @Column(name = "max_users")
    private Integer maxUsers;

    @Column(name = "current_users", nullable = false)
    private Integer currentUsers;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public RoleEntity withDescription(String description) {
        return this.toBuilder().description(description).build();
    }

    public RoleEntity withIsActive(boolean isActive) {
        return this.toBuilder().isActive(isActive).build();
    }

    public RoleEntity withCurrentUsers(Integer currentUsers) {
        return this.toBuilder().currentUsers(currentUsers).build();
    }

}
