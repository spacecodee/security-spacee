package com.spacecodee.securityspacee.session.application.mapper;

import java.util.List;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.response.SessionSummary;
import com.spacecodee.securityspacee.session.domain.model.Session;

public interface ISessionSummaryMapper {

    @NonNull
    SessionSummary toSummary(@NonNull Session session, boolean isCurrent);

    @NonNull
    List<SessionSummary> toSummaryList(@NonNull List<Session> sessions, @NonNull String currentSessionId);
}
