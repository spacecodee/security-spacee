package com.spacecodee.securityspacee.session.adapter.mapper.impl;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import com.spacecodee.securityspacee.session.adapter.mapper.ILogoutRemoteSessionMapper;
import com.spacecodee.securityspacee.session.application.command.LogoutRemoteSessionCommand;

@Component
public final class LogoutRemoteSessionMapperImpl implements ILogoutRemoteSessionMapper {

    private static final String DEFAULT_LOGOUT_REASON = "manual_logout_from_ui";

    @Override
    public @NonNull LogoutRemoteSessionCommand toCommand(@NonNull Integer userId, @NonNull String sessionId) {
        return new LogoutRemoteSessionCommand(
                userId,
                sessionId,
                DEFAULT_LOGOUT_REASON);
    }
}
