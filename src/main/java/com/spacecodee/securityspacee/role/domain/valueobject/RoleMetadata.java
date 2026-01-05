package com.spacecodee.securityspacee.role.domain.valueobject;

import java.time.Instant;
import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public final class RoleMetadata {

    private final Instant createdAt;
    private final Instant updatedAt;
    private final Integer version;

    RoleMetadata(
            Instant createdAt,
            Instant updatedAt,
            Integer version) {
        this.createdAt = Objects.requireNonNull(createdAt, "role.validation.metadata.created_at.required");
        this.updatedAt = updatedAt;
        this.version = Objects.requireNonNull(version, "role.validation.metadata.version.required");

        if (this.version < 1) {
            throw new IllegalArgumentException("role.validation.metadata.version.min_value");
        }
    }

    @Contract("_ -> new")
    public static @NonNull RoleMetadata forCreation(Instant timestamp) {
        return RoleMetadata.builder()
                .createdAt(timestamp)
                .updatedAt(null)
                .version(1)
                .build();
    }

    @Contract("_ -> new")
    public @NonNull RoleMetadata forUpdate(Instant timestamp) {
        return this.toBuilder()
                .updatedAt(timestamp)
                .version(this.version + 1)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RoleMetadata that = (RoleMetadata) o;
        return Objects.equals(this.createdAt, that.createdAt) &&
                Objects.equals(this.updatedAt, that.updatedAt) &&
                Objects.equals(this.version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.createdAt, this.updatedAt, this.version);
    }
}
