package com.spacecodee.securityspacee.session.application.command;

import org.jspecify.annotations.NonNull;

import lombok.Builder;

@Builder
public record UpdateSessionActivityCommand(
        @NonNull String sessionToken) {
}
