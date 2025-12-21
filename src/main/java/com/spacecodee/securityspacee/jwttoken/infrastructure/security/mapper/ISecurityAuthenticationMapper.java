package com.spacecodee.securityspacee.jwttoken.infrastructure.security.mapper;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;

import com.spacecodee.securityspacee.jwttoken.application.response.TokenValidationResponse;

public interface ISecurityAuthenticationMapper {

    @NonNull
    Authentication toAuthentication(@NonNull TokenValidationResponse response);
}
