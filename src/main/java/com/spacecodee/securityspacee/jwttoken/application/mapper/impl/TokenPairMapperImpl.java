package com.spacecodee.securityspacee.jwttoken.application.mapper.impl;

import com.spacecodee.securityspacee.jwttoken.application.mapper.ITokenPairMapper;
import com.spacecodee.securityspacee.jwttoken.application.response.TokenPairResponse;
import com.spacecodee.securityspacee.jwttoken.domain.valueobject.TokenPair;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

public final class TokenPairMapperImpl implements ITokenPairMapper {

    @Contract("_ -> new")
    @Override
    public @NonNull TokenPairResponse toResponse(@NonNull TokenPair domain) {
        return new TokenPairResponse(
                domain.getAccessToken(),
                domain.getRefreshToken(),
                domain.getExpiresIn());
    }
}
