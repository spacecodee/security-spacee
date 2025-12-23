package com.spacecodee.securityspacee.auth.adapter.mapper;

import com.spacecodee.securityspacee.auth.adapter.request.RefreshTokenRequest;
import com.spacecodee.securityspacee.jwttoken.application.command.RefreshTokenCommand;

import jakarta.servlet.http.HttpServletRequest;

public interface IRefreshTokenRequestMapper {

    RefreshTokenCommand toCommand(RefreshTokenRequest request, HttpServletRequest servletRequest);
}
