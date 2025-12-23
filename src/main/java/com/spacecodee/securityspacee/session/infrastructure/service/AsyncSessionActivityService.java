package com.spacecodee.securityspacee.session.infrastructure.service;

import java.time.Instant;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.spacecodee.securityspacee.session.application.command.UpdateSessionActivityCommand;
import com.spacecodee.securityspacee.session.application.port.in.IUpdateSessionActivityUseCase;
import com.spacecodee.securityspacee.session.domain.valueobject.SessionId;

@Service
public class AsyncSessionActivityService {

    private static final Logger log = LoggerFactory.getLogger(AsyncSessionActivityService.class);

    private final IUpdateSessionActivityUseCase updateSessionActivityUseCase;

    public AsyncSessionActivityService(@NonNull IUpdateSessionActivityUseCase updateSessionActivityUseCase) {
        this.updateSessionActivityUseCase = updateSessionActivityUseCase;
    }

    @Async("sessionActivityExecutor")
    public void updateActivityAsync(@NonNull SessionId sessionId, @NonNull Instant timestamp) {
        try {
            this.updateSessionActivityUseCase.execute(
                    new UpdateSessionActivityCommand(sessionId, timestamp));
        } catch (Exception e) {
            log.error("Error updating session activity for {}: {}", sessionId, e.getMessage(), e);
        }
    }
}
