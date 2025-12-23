package com.spacecodee.securityspacee.session.adapter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spacecodee.securityspacee.session.adapter.response.HeartbeatResponse;
import com.spacecodee.securityspacee.session.adapter.response.SessionStatusResponse;
import com.spacecodee.securityspacee.shared.adapter.in.web.dto.ApiDataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Session Activity", description = "Session activity and expiration management endpoints")
@RequestMapping("/session")
@SecurityRequirement(name = "Bearer Authentication")
public interface ISessionActivityController {

    @Operation(summary = "Heartbeat", description = "Keep the session alive by updating last activity timestamp. Prevents idle timeout expiration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session is alive. Returns remaining time information.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiDataResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication token", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/heartbeat")
    ResponseEntity<ApiDataResponse<HeartbeatResponse>> heartbeat();

    @Operation(summary = "Session Status", description = "Get current session expiration status including remaining idle and absolute time, and warning information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session status retrieved successfully.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiDataResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication token", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found - Session not found", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/status")
    ResponseEntity<ApiDataResponse<SessionStatusResponse>> status();
}
