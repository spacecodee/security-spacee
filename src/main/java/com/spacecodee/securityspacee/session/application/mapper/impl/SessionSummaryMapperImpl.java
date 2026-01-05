package com.spacecodee.securityspacee.session.application.mapper.impl;

import java.util.List;

import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.mapper.ISessionSummaryMapper;
import com.spacecodee.securityspacee.session.application.response.SessionSummary;
import com.spacecodee.securityspacee.session.domain.model.Session;

public final class SessionSummaryMapperImpl implements ISessionSummaryMapper {

    @Override
    public @NonNull SessionSummary toSummary(@NonNull Session session, boolean isCurrent) {
        final String location = session.getMetadata().getLocation() != null
                ? session.getMetadata().getLocation().friendlyName()
                : "Unknown Location";

        final String deviceName = session.getMetadata().getDeviceName() != null
                ? session.getMetadata().getDeviceName()
                : "Unknown Device";

        return SessionSummary.builder()
                .sessionId(session.getSessionId().getValue().toString())
                .deviceName(deviceName)
                .location(location)
                .ipAddress(session.getMetadata().getIpAddress())
                .createdAt(session.getMetadata().getCreatedAt())
                .lastActivityAt(session.getMetadata().getLastActivityAt())
                .isCurrent(isCurrent)
                .isTrustedDevice(session.getMetadata().isTrustedDevice())
                .build();
    }

    @Override
    public @NonNull @Unmodifiable List<SessionSummary> toSummaryList(@NonNull List<Session> sessions,
                                                                     @NonNull String currentSessionId) {
        return sessions.stream()
                .map(session -> this.toSummary(session,
                        session.getSessionId().getValue().toString().equals(currentSessionId)))
                .toList();
    }
}
