package com.spacecodee.securityspacee.auth.adapter.mapper.impl;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.auth.adapter.mapper.IRefreshTokenRequestMapper;
import com.spacecodee.securityspacee.auth.adapter.request.RefreshTokenRequest;
import com.spacecodee.securityspacee.jwttoken.application.command.RefreshTokenCommand;
import com.spacecodee.securityspacee.shared.application.port.out.IClientIpExtractorPort;

import jakarta.servlet.http.HttpServletRequest;

public final class RefreshTokenRequestMapperImpl implements IRefreshTokenRequestMapper {

    private final IClientIpExtractorPort clientIpExtractorPort;

    public RefreshTokenRequestMapperImpl(@NonNull IClientIpExtractorPort clientIpExtractorPort) {
        this.clientIpExtractorPort = clientIpExtractorPort;
    }

    @Override
    public @NonNull RefreshTokenCommand toCommand(@NonNull RefreshTokenRequest request,
                                                  HttpServletRequest servletRequest) {
        String ipAddress = this.clientIpExtractorPort.extractClientIp(servletRequest);
        String userAgent = servletRequest.getHeader("User-Agent");

        return new RefreshTokenCommand(
                request.refreshToken(),
                ipAddress,
                userAgent);
    }
}
