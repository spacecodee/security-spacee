package com.spacecodee.securityspacee.session.application.response;

import java.util.List;

import org.jspecify.annotations.NonNull;

import lombok.Builder;

@Builder
public record ActiveSessionsResponse(
        @NonNull List<SessionSummary> activeSessions,
        int totalActive,
        int maxAllowed) {
}
