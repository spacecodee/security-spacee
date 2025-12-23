package com.spacecodee.securityspacee.session.infrastructure.persistence.mapper;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.session.domain.model.Session;
import com.spacecodee.securityspacee.session.infrastructure.persistence.jpa.SessionEntity;

public interface ISessionPersistenceMapper {

    @NonNull
    SessionEntity toEntity(@NonNull Session session);

    @NonNull
    Session toDomain(@NonNull SessionEntity entity);
}
