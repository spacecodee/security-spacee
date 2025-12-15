package com.spacecodee.securityspacee.user.infrastructure.persistence.mapper;

import com.spacecodee.securityspacee.user.domain.model.User;
import com.spacecodee.securityspacee.user.infrastructure.persistence.jpa.UserAuthEntity;

public interface IUserPersistenceMapper {

    UserAuthEntity toEntity(User user);

    User toDomain(UserAuthEntity entity);
}
