package com.spacecodee.securityspacee.role.adapter.request;

import org.jspecify.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateRoleRequest(

        @NotNull(message = "{role.validation.role_name.required}")
        @NotBlank(message = "{role.validation.role_name.required}")
        @Size(min = 3, max = 50, message = "{role.validation.role_name.length}")
        @Pattern(regexp = "^[A-Z0-9_]+$", message = "{role.validation.role_name.invalid_format}")
        @JsonProperty("name") String name,

        @NotNull(message = "{role.validation.description.required}")
        @NotBlank(message = "{role.validation.description.required}")
        @Size(max = 500, message = "{role.validation.description.max_length}")
        @JsonProperty("description") String description,

        @Nullable
        @JsonProperty("parent_role_id") Integer parentRoleId,

        @Nullable
        @Pattern(regexp = "^(ADMIN|USER|AUDITOR)$", message = "{role.validation.system_role_tag.invalid}")
        @JsonProperty("system_role_tag") String systemRoleTag,

        @Nullable
        @Min(value = 1, message = "{role.validation.max_users.min_value}")
        @Max(value = 1_000_000, message = "{role.validation.max_users.max_value}")
        @JsonProperty("max_users") Integer maxUsers,

        @JsonProperty("is_active") boolean isActive) {
}
