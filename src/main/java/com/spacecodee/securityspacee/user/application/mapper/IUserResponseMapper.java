package com.spacecodee.securityspacee.user.application.mapper;

import com.spacecodee.securityspacee.user.application.response.UserResponse;
import com.spacecodee.securityspacee.user.domain.model.User;

public interface IUserResponseMapper {

    UserResponse toResponse(User user);
}
