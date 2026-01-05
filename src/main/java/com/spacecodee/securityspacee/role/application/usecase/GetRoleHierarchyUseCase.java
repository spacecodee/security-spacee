package com.spacecodee.securityspacee.role.application.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.spacecodee.securityspacee.role.application.port.in.IGetRoleHierarchyUseCase;
import com.spacecodee.securityspacee.role.application.query.GetRoleHierarchyQuery;
import com.spacecodee.securityspacee.role.application.response.RoleHierarchyResponse;
import com.spacecodee.securityspacee.role.domain.exception.RoleNotFoundException;
import com.spacecodee.securityspacee.role.domain.model.Role;
import com.spacecodee.securityspacee.role.domain.model.RoleHierarchyNode;
import com.spacecodee.securityspacee.role.domain.repository.IRoleRepository;
import com.spacecodee.securityspacee.role.domain.valueobject.RoleId;

public class GetRoleHierarchyUseCase implements IGetRoleHierarchyUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetRoleHierarchyUseCase.class);

    private final IRoleRepository roleRepository;

    public GetRoleHierarchyUseCase(IRoleRepository roleRepository) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull RoleHierarchyResponse execute(@NonNull GetRoleHierarchyQuery query) {
        Objects.requireNonNull(query);
        log.info("Getting hierarchy for role ID: {}", query.roleId());

        Role role = this.roleRepository.findById(RoleId.of(query.roleId()))
                .orElseThrow(() -> new RoleNotFoundException("role.exception.role_not_found"));

        RoleHierarchyNode parent = this.getParentNode(role);
        List<RoleHierarchyNode> children = this.getChildrenNodes(role);
        List<RoleHierarchyNode> ancestors = this.getAncestorNodes(role);

        return new RoleHierarchyResponse(
                role.getRoleId().getValue(),
                role.getRoleName().getValue(),
                role.getHierarchyLevel(),
                parent,
                children,
                ancestors);
    }

    private RoleHierarchyNode getParentNode(@NonNull Role role) {
        if (role.getParentRoleId() == null) {
            return null;
        }

        return this.roleRepository.findById(role.getParentRoleId())
                .map(parent -> new RoleHierarchyNode(
                        parent.getRoleId().getValue(),
                        parent.getRoleName().getValue(),
                        parent.getHierarchyLevel()))
                .orElse(null);
    }

    private @NonNull List<RoleHierarchyNode> getChildrenNodes(@NonNull Role role) {
        return this.roleRepository.findByParentRoleId(role.getRoleId()).stream()
                .map(child -> new RoleHierarchyNode(
                        child.getRoleId().getValue(),
                        child.getRoleName().getValue(),
                        child.getHierarchyLevel()))
                .toList();
    }

    private @NonNull List<RoleHierarchyNode> getAncestorNodes(@NonNull Role role) {
        List<RoleHierarchyNode> ancestors = new ArrayList<>();
        Role current = role;

        while (current.getParentRoleId() != null) {
            Role parent = this.roleRepository.findById(current.getParentRoleId()).orElse(null);
            if (parent == null) {
                break;
            }

            ancestors.add(new RoleHierarchyNode(
                    parent.getRoleId().getValue(),
                    parent.getRoleName().getValue(),
                    parent.getHierarchyLevel()));

            current = parent;
        }

        return ancestors;
    }
}
