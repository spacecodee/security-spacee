package com.spacecodee.securityspacee.session.adapter.mapper;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.application.command.LogoutRemoteSessionCommand;

public interface ILogoutRemoteSessionMapper {

    @NonNull
    LogoutRemoteSessionCommand toCommand(@NonNull Integer userId, @NonNull String sessionId);
}
