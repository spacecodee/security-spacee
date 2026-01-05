package com.spacecodee.securityspacee.session.adapter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spacecodee.securityspacee.session.adapter.request.LogoutAllRequest;
import com.spacecodee.securityspacee.shared.adapter.in.web.dto.ApiDataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Tag(name = "Session Management", description = "Multi-device session management endpoints")
@RequestMapping("/user/sessions")
@SecurityRequirement(name = "Bearer Authentication")
public interface ISessionController {

    @Operation(summary = "List Active Sessions", description = "Retrieve all active sessions for the authenticated user. Shows device information, location, and last activity for each session.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully. Returns list of active sessions with device and location details.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiDataResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication token", content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    ResponseEntity<ApiDataResponse<Object>> getActiveSessions(HttpServletRequest servletRequest);

    @Operation(summary = "Logout Remote Session", description = "Force logout a specific session by ID. Allows users to close sessions from other devices remotely. Only the session owner can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session closed successfully. The target session has been terminated.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiDataResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication token", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - You are not authorized to logout this session", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found - Session does not exist or has already been terminated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict - Session already logged out", content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{sessionId}")
    ResponseEntity<ApiDataResponse<Object>> logoutRemoteSession(@PathVariable String sessionId);

    @Operation(summary = "Logout All Sessions", description = "Close all active sessions for the authenticated user. Optionally includes current session. Useful for security scenarios like password change.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All sessions closed successfully. Returns count and list of closed sessions.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiDataResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication token", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/logout-all")
    ResponseEntity<ApiDataResponse<Object>> logoutAllSessions(@Valid @RequestBody LogoutAllRequest request);
}
