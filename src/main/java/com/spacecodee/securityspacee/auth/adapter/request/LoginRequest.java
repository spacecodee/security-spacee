package com.spacecodee.securityspacee.auth.adapter.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotNull(message = "{auth.validation.username_or_email.required}")
        @NotBlank(message = "{auth.validation.username_or_email.blank}")
        @Size(min = 3, max = 255, message = "{auth.validation.username_or_email.size}")
        @JsonProperty("usernameOrEmail") String usernameOrEmail,

        @NotNull(message = "{auth.validation.password.required}")
        @NotBlank(message = "{auth.validation.password.blank}")
        @Size(min = 8, max = 100, message = "{auth.validation.password.size}")
        @JsonProperty("password") String password) {
}
