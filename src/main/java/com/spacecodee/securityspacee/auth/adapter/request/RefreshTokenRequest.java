package com.spacecodee.securityspacee.auth.adapter.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RefreshTokenRequest(

        @NotNull(message = "{auth.validation.refresh_token.required}")
        @NotBlank(message = "{auth.validation.refresh_token.blank}")
        @JsonProperty("refreshToken") String refreshToken) {
}
