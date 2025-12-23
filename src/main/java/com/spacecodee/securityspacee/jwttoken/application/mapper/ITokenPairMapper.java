package com.spacecodee.securityspacee.jwttoken.application.mapper;

import com.spacecodee.securityspacee.jwttoken.application.response.TokenPairResponse;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenPair;

public interface ITokenPairMapper {

    TokenPairResponse toResponse(TokenPair domain);
}
