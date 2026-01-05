package com.spacecodee.securityspacee.jwttoken.infrastructure.security.mapper;

import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.spacecodee.securityspacee.jwttoken.application.response.TokenValidationResponse;

public final class SecurityAuthenticationMapperImpl implements ISecurityAuthenticationMapper {

    @Override
    @NonNull
    public Authentication toAuthentication(@NonNull TokenValidationResponse response) {
        var authorities = response.roles()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(
                response.username(),
                null,
                authorities);
    }
}
