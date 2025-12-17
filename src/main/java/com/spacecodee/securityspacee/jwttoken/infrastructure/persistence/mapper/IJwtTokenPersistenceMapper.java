package com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.mapper;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.jwttoken.domain.model.JwtToken;
import com.spacecodee.securityspacee.jwttoken.infrastructure.persistence.jpa.JwtTokenEntity;

public interface IJwtTokenPersistenceMapper {

    @NonNull
    JwtTokenEntity toEntity(@NonNull JwtToken jwtToken);

    @NonNull
    JwtToken toDomain(@NonNull JwtTokenEntity entity);
}
