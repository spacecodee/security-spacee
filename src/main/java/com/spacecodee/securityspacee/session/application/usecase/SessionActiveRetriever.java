package com.spacecodee.securityspacee.session.application.usecase;

import java.util.List;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.command.GetActiveSessionsQuery;
import com.spacecodee.securityspacee.session.application.mapper.ISessionSummaryMapper;
import com.spacecodee.securityspacee.session.application.port.in.IGetActiveSessionsUseCase;
import com.spacecodee.securityspacee.session.application.response.ActiveSessionsResponse;
import com.spacecodee.securityspacee.session.application.response.SessionSummary;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.service.ISessionPolicyService;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionPolicy;

public final class SessionActiveRetriever implements IGetActiveSessionsUseCase {

    private final ISessionRepository sessionRepository;
    private final ISessionSummaryMapper sessionSummaryMapper;
    private final ISessionPolicyService sessionPolicyService;

    public SessionActiveRetriever(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ISessionSummaryMapper sessionSummaryMapper,
            @NonNull ISessionPolicyService sessionPolicyService) {
        this.sessionRepository = sessionRepository;
        this.sessionSummaryMapper = sessionSummaryMapper;
        this.sessionPolicyService = sessionPolicyService;
    }

    @Override
    public @NonNull ActiveSessionsResponse execute(@NonNull GetActiveSessionsQuery query) {
        final List<Session> activeSessions = this.sessionRepository.findActiveByUserId(query.userId());

        final List<SessionSummary> summaries = this.sessionSummaryMapper.toSummaryList(
                activeSessions,
                query.currentSessionId());

        final SessionPolicy policy = this.sessionPolicyService.getPolicyForUser(query.userId());

        return ActiveSessionsResponse.builder()
                .activeSessions(summaries)
                .totalActive(activeSessions.size())
                .maxAllowed(policy.maxSessions())
                .build();
    }
}
