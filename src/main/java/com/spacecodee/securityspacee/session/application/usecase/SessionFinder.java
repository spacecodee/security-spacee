package com.spacecodee.securityspacee.session.application.usecase;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.mapper.ISessionResponseMapper;
import com.spacecodee.securityspacee.session.application.port.in.IGetSessionUseCase;
import com.spacecodee.securityspacee.session.application.response.SessionResponse;
import com.spacecodee.securityspacee.session.domain.exception.SessionNotFoundException;
import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.domain.repository.ISessionRepository;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionToken;

public final class SessionFinder implements IGetSessionUseCase {

    private final ISessionRepository sessionRepository;
    private final ISessionResponseMapper responseMapper;

    public SessionFinder(
            @NonNull ISessionRepository sessionRepository,
            @NonNull ISessionResponseMapper responseMapper) {
        this.sessionRepository = sessionRepository;
        this.responseMapper = responseMapper;
    }

    @Override
    public @NonNull SessionResponse execute(@NonNull String sessionToken) {
        SessionToken token = SessionToken.parse(sessionToken);

        Session session = this.sessionRepository.findBySessionToken(token)
                .orElseThrow(SessionNotFoundException::new);

        return this.responseMapper.toResponse(session);
    }
}
