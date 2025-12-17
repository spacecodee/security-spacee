package com.spacecodee.securityspacee.auth.adapter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spacecodee.securityspacee.auth.adapter.request.LoginRequest;
import com.spacecodee.securityspacee.shared.adapter.in.web.dto.ApiDataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Authentication", description = "User authentication and session management endpoints")
@RequestMapping("/auth")
public interface IAuthController {

    @Operation(summary = "User Login", description = "Authenticate user with credentials (username or email + password). Returns JWT tokens and user information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful. Returns access token, refresh token, and user details.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiDataResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or validation error", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid username/email or password", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Account inactive", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "423", description = "Locked - Account locked due to too many failed login attempts", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    ResponseEntity<ApiDataResponse<Object>> login(
            @Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest);
}
