package com.spacecodee.securityspacee.session.application.command;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.valueobject.LogoutReason;

import lombok.Builder;

@Builder
public record LogoutSessionCommand(
        @NonNull String sessionToken,
        @NonNull LogoutReason logoutReason) {
}
