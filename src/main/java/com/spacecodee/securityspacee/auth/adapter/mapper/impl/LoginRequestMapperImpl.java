package com.spacecodee.securityspacee.auth.adapter.mapper.impl;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.auth.adapter.mapper.ILoginRequestMapper;
import com.spacecodee.securityspacee.auth.adapter.request.LoginRequest;
import com.spacecodee.securityspacee.auth.application.command.LoginCommand;
import com.spacecodee.securityspacee.shared.application.port.out.IClientIpExtractorPort;

import jakarta.servlet.http.HttpServletRequest;

public final class LoginRequestMapperImpl implements ILoginRequestMapper {

    private final IClientIpExtractorPort clientIpExtractorPort;

    public LoginRequestMapperImpl(@NonNull IClientIpExtractorPort clientIpExtractorPort) {
        this.clientIpExtractorPort = clientIpExtractorPort;
    }

    @Override
    public @NonNull LoginCommand toCommand(@NonNull LoginRequest request, HttpServletRequest servletRequest) {
        String ipAddress = this.clientIpExtractorPort.extractClientIp(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");

        return new LoginCommand(
                request.usernameOrEmail(),
                request.password(),
                ipAddress,
                userAgent);
    }
}
