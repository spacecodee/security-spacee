package com.spacecodee.securityspacee.auth.adapter.mapper;

import com.spacecodee.securityspacee.auth.adapter.request.LoginRequest;
import com.spacecodee.securityspacee.auth.application.command.LoginCommand;

import jakarta.servlet.http.HttpServletRequest;

public interface ILoginRequestMapper {

    LoginCommand toCommand(LoginRequest request, HttpServletRequest servletRequest);
}
