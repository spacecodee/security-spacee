package com.spacecodee.securityspacee.user.adapter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spacecodee.securityspacee.user.adapter.request.RegisterUserRequest;
import com.spacecodee.securityspacee.user.application.response.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "User Management", description = "Endpoints for user registration and profile management")
@RequestMapping("/user")
public interface IUserController {

    @Operation(summary = "Register a new user", description = """
            Registers a new user in the system. Supports HUMAN, SYSTEM, and SERVICE user types.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data (validation error)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Username or email already exists (duplicate conflict)", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/register")
    ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegisterUserRequest request);
}
