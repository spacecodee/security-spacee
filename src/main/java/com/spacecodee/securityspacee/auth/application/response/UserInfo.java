package com.spacecodee.securityspacee.auth.application.response;

import java.util.List;

import com.spacecodee.securityspacee.user.domain.valueobject.UserType;

public record UserInfo(
        Integer userId,
        String username,
        String email,
        UserType userType,
        List<String> roles) {
}
