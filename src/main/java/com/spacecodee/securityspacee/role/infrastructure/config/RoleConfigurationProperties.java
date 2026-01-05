package com.spacecodee.securityspacee.role.infrastructure.config;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Validated
@ConfigurationProperties(prefix = "app.role")
public record RoleConfigurationProperties(
        @NonNull @Min(1) @Max(10) Integer maxHierarchyDepth) {
}
