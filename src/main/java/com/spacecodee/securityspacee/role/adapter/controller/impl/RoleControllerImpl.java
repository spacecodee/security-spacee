package com.spacecodee.securityspacee.role.adapter.controller.impl;

import java.util.Objects;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.spacecodee.securityspacee.role.adapter.controller.IRoleController;
import com.spacecodee.securityspacee.role.adapter.mapper.IRoleRestMapper;
import com.spacecodee.securityspacee.role.adapter.request.CreateRoleRequest;
import com.spacecodee.securityspacee.role.application.command.CreateRoleCommand;
import com.spacecodee.securityspacee.role.application.port.in.ICreateRoleUseCase;
import com.spacecodee.securityspacee.role.application.response.RoleResponse;

import jakarta.validation.Valid;

@RestController
public final class RoleControllerImpl implements IRoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleControllerImpl.class);

    private final ICreateRoleUseCase createRoleUseCase;
    private final IRoleRestMapper restMapper;

    public RoleControllerImpl(
            ICreateRoleUseCase createRoleUseCase,
            IRoleRestMapper restMapper) {
        this.createRoleUseCase = Objects.requireNonNull(createRoleUseCase);
        this.restMapper = Objects.requireNonNull(restMapper);
    }

    @Override
    public @NonNull ResponseEntity<RoleResponse> createRole(@Valid @NonNull CreateRoleRequest request) {
        log.info("POST /admin/roles - name: {}", request.name());

        CreateRoleCommand command = this.restMapper.toCommand(request);

        RoleResponse response = this.createRoleUseCase.execute(command);

        log.info("Role created successfully - roleId: {}, name: {}",
                response.roleId(), response.name());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
