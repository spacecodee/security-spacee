package com.spacecodee.securityspacee.role.adapter.controller.impl;

import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.spacecodee.securityspacee.role.adapter.controller.IRoleHierarchyController;
import com.spacecodee.securityspacee.role.application.port.in.IGetRoleHierarchyUseCase;
import com.spacecodee.securityspacee.role.application.query.GetRoleHierarchyQuery;
import com.spacecodee.securityspacee.role.application.response.RoleHierarchyResponse;

@RestController
public class RoleHierarchyControllerImpl implements IRoleHierarchyController {

    private static final Logger log = LoggerFactory.getLogger(RoleHierarchyControllerImpl.class);

    private final IGetRoleHierarchyUseCase getRoleHierarchyUseCase;

    public RoleHierarchyControllerImpl(IGetRoleHierarchyUseCase getRoleHierarchyUseCase) {
        this.getRoleHierarchyUseCase = Objects.requireNonNull(getRoleHierarchyUseCase);
    }

    @Override
    public @NonNull ResponseEntity<RoleHierarchyResponse> getRoleHierarchy(Integer id) {
        Objects.requireNonNull(id);
        log.info("GET /admin/roles/{}/hierarchy - Retrieving role hierarchy", id);

        GetRoleHierarchyQuery query = new GetRoleHierarchyQuery(id);
        RoleHierarchyResponse response = this.getRoleHierarchyUseCase.execute(query);

        log.info("Role hierarchy retrieved for ID: {}", id);
        return ResponseEntity.ok(response);
    }
}
