package com.spacecodee.securityspacee.session.adapter.request;

import org.jspecify.annotations.NonNull;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LogoutAllRequest(
        @NonNull @NotNull(message = "{session.validation.include_current_session.required}") Boolean includeCurrentSession) {
}
