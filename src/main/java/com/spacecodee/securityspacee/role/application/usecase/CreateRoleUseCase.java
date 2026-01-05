package com.spacecodee.securityspacee.role.application.usecase;

import java.util.Objects;
import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import com.spacecodee.securityspacee.role.application.command.CreateRoleCommand;
import com.spacecodee.securityspacee.role.application.mapper.IRoleResponseMapper;
import com.spacecodee.securityspacee.role.application.port.in.ICreateRoleUseCase;
import com.spacecodee.securityspacee.role.application.response.RoleResponse;
import com.spacecodee.securityspacee.role.domain.event.RoleCreatedEvent;
import com.spacecodee.securityspacee.role.domain.exception.InactiveParentRoleException;
import com.spacecodee.securityspacee.role.domain.exception.MaxHierarchyDepthExceededException;
import com.spacecodee.securityspacee.role.domain.exception.ParentRoleNotFoundException;
import com.spacecodee.securityspacee.role.domain.exception.RoleAlreadyExistsException;
import com.spacecodee.securityspacee.role.domain.exception.SystemRoleTagAlreadyUsedException;
import com.spacecodee.securityspacee.role.domain.model.Role;
import com.spacecodee.securityspacee.role.domain.repository.IRoleRepository;
import com.spacecodee.securityspacee.role.domain.valueobject.Description;
import com.spacecodee.securityspacee.role.domain.valueobject.MaxUsers;
import com.spacecodee.securityspacee.role.domain.valueobject.RoleId;
import com.spacecodee.securityspacee.role.domain.valueobject.RoleName;
import com.spacecodee.securityspacee.role.domain.valueobject.SystemRoleTag;
import com.spacecodee.securityspacee.role.infrastructure.config.RoleConfigurationProperties;

public class CreateRoleUseCase implements ICreateRoleUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateRoleUseCase.class);

    private final IRoleRepository roleRepository;
    private final IRoleResponseMapper roleResponseMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final int maxHierarchyDepth;

    public CreateRoleUseCase(
            IRoleRepository roleRepository,
            IRoleResponseMapper roleResponseMapper,
            ApplicationEventPublisher eventPublisher,
            RoleConfigurationProperties roleProperties) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.roleResponseMapper = Objects.requireNonNull(roleResponseMapper);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.maxHierarchyDepth = Objects.requireNonNull(roleProperties).maxHierarchyDepth();
    }

    @Override
    @Transactional
    public @NonNull RoleResponse execute(@NonNull CreateRoleCommand command) {
        Objects.requireNonNull(command);

        log.info("Starting role creation for name: {}", command.name());

        RoleName roleName = RoleName.of(command.name());

        this.validateRoleNameUniqueness(roleName);

        SystemRoleTag systemRoleTag = null;
        if (command.systemRoleTag() != null) {
            systemRoleTag = SystemRoleTag.of(command.systemRoleTag());
            this.validateSystemRoleTagUniqueness(systemRoleTag);
        }

        RoleId parentRoleId = null;
        int hierarchyLevel = 1;
        String parentRoleName = null;

        if (command.parentRoleId() != null) {
            RoleId requestedParentRoleId = RoleId.of(command.parentRoleId());
            Optional<Role> parentRoleOpt = this.roleRepository.findById(requestedParentRoleId);

            if (parentRoleOpt.isEmpty()) {
                log.warn("Parent role not found with ID: {}", command.parentRoleId());
                throw new ParentRoleNotFoundException("role.exception.parent_role_not_found");
            }

            Role parentRole = parentRoleOpt.get();

            if (!parentRole.isActive()) {
                log.warn("Parent role is inactive: {}", parentRole.getRoleName().getValue());
                throw new InactiveParentRoleException("role.exception.inactive_parent_role");
            }

            hierarchyLevel = parentRole.getHierarchyLevel() + 1;

            if (hierarchyLevel > this.maxHierarchyDepth) {
                log.warn("Max hierarchy depth exceeded. Attempted level: {}", hierarchyLevel);
                throw new MaxHierarchyDepthExceededException("role.exception.max_hierarchy_depth_exceeded");
            }

            parentRoleId = parentRole.getRoleId();
            parentRoleName = parentRole.getRoleName().getValue();
        }

        Description description = Description.of(command.description());
        MaxUsers maxUsers = command.maxUsers() != null ? MaxUsers.of(command.maxUsers()) : null;

        Role role = Role.create(
                roleName,
                description,
                parentRoleId,
                hierarchyLevel,
                systemRoleTag,
                maxUsers);

        Role savedRole = this.roleRepository.save(role);

        log.info("Role created successfully with ID: {}", savedRole.getRoleId().getValue());

        this.publishRoleCreatedEvent(savedRole);

        return this.roleResponseMapper.toResponse(savedRole, parentRoleName, "admin");
    }

    private void validateRoleNameUniqueness(RoleName roleName) {
        if (this.roleRepository.existsByName(roleName)) {
            log.warn("Role creation failed: Role name '{}' already exists", roleName.getValue());
            throw new RoleAlreadyExistsException("role.exception.role_already_exists", roleName.getValue());
        }
    }

    private void validateSystemRoleTagUniqueness(SystemRoleTag systemRoleTag) {
        if (this.roleRepository.existsBySystemRoleTag(systemRoleTag)) {
            log.warn("Role creation failed: System role tag '{}' already used", systemRoleTag.name());
            throw new SystemRoleTagAlreadyUsedException(
                    "role.exception.system_role_tag_already_used",
                    systemRoleTag.name());
        }
    }

    private void publishRoleCreatedEvent(@NonNull Role role) {
        RoleCreatedEvent event = new RoleCreatedEvent(
                role.getRoleId().getValue(),
                role.getRoleName().getValue(),
                role.getDescription().getValue(),
                role.getHierarchyLevel(),
                role.getParentRoleId() != null ? role.getParentRoleId().getValue() : null,
                role.getSystemRoleTag() != null ? role.getSystemRoleTag().name() : null,
                role.getMaxUsers() != null ? role.getMaxUsers().getValue() : null,
                role.getMetadata().getCreatedAt());

        this.eventPublisher.publishEvent(event);
        log.debug("Published RoleCreatedEvent for role: {}", role.getRoleName().getValue());
    }
}
