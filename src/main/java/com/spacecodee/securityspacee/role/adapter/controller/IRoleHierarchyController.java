package com.spacecodee.securityspacee.role.adapter.controller;

import com.spacecodee.securityspacee.role.application.response.RoleHierarchyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jspecify.annotations.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Role Hierarchy", description = "Endpoints for role hierarchy visualization and management")
@RequestMapping("/admin/roles")
@SecurityRequirement(name = "Bearer Authentication")
public interface IRoleHierarchyController {

    @Operation(summary = "Get role hierarchy", description = "Retrieves the complete hierarchy structure for a specific role, including parent, children, and all ancestors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hierarchy retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleHierarchyResponse.class))),
            @ApiResponse(responseCode = "404", description = "Role not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions", content = @Content)
    })
    @GetMapping("/{id}/hierarchy")
    @NonNull
    ResponseEntity<RoleHierarchyResponse> getRoleHierarchy(
            @Parameter(description = "Role ID", required = true, example = "1") @PathVariable Integer id);
}
