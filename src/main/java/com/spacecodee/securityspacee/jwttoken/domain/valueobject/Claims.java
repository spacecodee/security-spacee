package com.spacecodee.securityspacee.jwttoken.domain.valueobject;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class Claims {

    private static final String ROLES_CLAIM = "roles";
    private static final String IAT_CLAIM = "iat";
    private static final String EXP_CLAIM = "exp";

    private final String jti;
    private final String sub;
    private final String username;
    @Builder.Default
    private final List<String> roles = List.of();
    private final String sessionId;
    private final Instant iat;
    private final Instant exp;
    private final String iss;
    private final String aud;
    private final String tokenType;

    public @NonNull Map<String, Object> toMap() {
        Map<String, Object> claims = new HashMap<>();
        if (this.jti != null)
            claims.put("jti", this.jti);
        if (this.sub != null)
            claims.put("sub", this.sub);
        if (this.username != null)
            claims.put("username", this.username);
        if (this.roles != null && !this.roles.isEmpty())
            claims.put(ROLES_CLAIM, this.roles);
        if (this.sessionId != null)
            claims.put("sessionId", this.sessionId);
        if (this.iat != null)
            claims.put(IAT_CLAIM, this.iat.getEpochSecond());
        if (this.exp != null)
            claims.put(EXP_CLAIM, this.exp.getEpochSecond());
        if (this.iss != null)
            claims.put("iss", this.iss);
        if (this.aud != null)
            claims.put("aud", this.aud);
        if (this.tokenType != null)
            claims.put("tokenType", this.tokenType);
        return claims;
    }

    @Contract("_ -> new")
    @SuppressWarnings("unchecked")
    public static @NonNull Claims fromMap(@NonNull Map<String, Object> map) {
        return Claims.builder()
                .jti((String) map.get("jti"))
                .sub((String) map.get("sub"))
                .username((String) map.get("username"))
                .roles(map.containsKey(ROLES_CLAIM) ? (List<String>) map.get(ROLES_CLAIM) : List.of())
                .sessionId((String) map.get("sessionId"))
                .iat(map.containsKey(IAT_CLAIM) ? Instant.ofEpochSecond(((Number) map.get(IAT_CLAIM)).longValue())
                        : null)
                .exp(map.containsKey(EXP_CLAIM) ? Instant.ofEpochSecond(((Number) map.get(EXP_CLAIM)).longValue())
                        : null)
                .iss((String) map.get("iss"))
                .aud((String) map.get("aud"))
                .tokenType((String) map.get("tokenType"))
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Claims claims = (Claims) o;
        return Objects.equals(this.jti, claims.jti);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.jti);
    }
}
