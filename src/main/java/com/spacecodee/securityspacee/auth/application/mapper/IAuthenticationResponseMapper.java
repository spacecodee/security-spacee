package com.spacecodee.securityspacee.auth.application.mapper;

import com.spacecodee.securityspacee.auth.application.port.out.TokenPair;
import com.spacecodee.securityspacee.auth.application.response.AuthenticationResponse;
import com.spacecodee.securityspacee.auth.domain.valueobject.AuthenticationResult;

public interface IAuthenticationResponseMapper {

    AuthenticationResponse toResponse(AuthenticationResult authResult, TokenPair tokenPair);
}
