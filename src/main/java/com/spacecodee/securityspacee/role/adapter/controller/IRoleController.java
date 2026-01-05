package com.spacecodee.securityspacee.role.adapter.controller;

import com.spacecodee.securityspacee.role.adapter.request.CreateRoleRequest;
import com.spacecodee.securityspacee.role.application.response.RoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Role Management", description = "Endpoints for role creation and management")
@RequestMapping("/admin/roles")
@SecurityRequirement(name = "Bearer Authentication")
public interface IRoleController {

    @Operation(summary = "Create a new role", description = """
            Creates a new role in the system with optional hierarchy, system tag, and user limits.
            Only accessible by users with ADMIN role.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Role created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data (validation error)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Parent role not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Role name or system tag already exists", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    ResponseEntity<RoleResponse> createRole(
            @Valid @RequestBody CreateRoleRequest request);
}
