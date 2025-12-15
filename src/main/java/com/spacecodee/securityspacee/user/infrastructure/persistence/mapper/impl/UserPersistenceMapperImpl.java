package com.spacecodee.securityspacee.user.infrastructure.persistence.mapper.impl;

import java.util.Objects;

import org.jspecify.annotations.NonNull;

import com.spacecodee.securityspacee.user.domain.model.User;
import com.spacecodee.securityspacee.user.domain.model.UserProfile;
import com.spacecodee.securityspacee.user.domain.model.UserProfileSnapshot;
import com.spacecodee.securityspacee.user.domain.model.UserSnapshot;
import com.spacecodee.securityspacee.user.domain.valueobject.Email;
import com.spacecodee.securityspacee.user.domain.valueobject.Password;
import com.spacecodee.securityspacee.user.domain.valueobject.Username;
import com.spacecodee.securityspacee.user.infrastructure.persistence.jpa.UserAuthEntity;
import com.spacecodee.securityspacee.user.infrastructure.persistence.jpa.UserProfileEntity;
import com.spacecodee.securityspacee.user.infrastructure.persistence.mapper.IUserPersistenceMapper;

public final class UserPersistenceMapperImpl implements IUserPersistenceMapper {

    @Override
    public UserAuthEntity toEntity(User user) {
        Objects.requireNonNull(user);

        UserAuthEntity authEntity = UserAuthEntity.builder()
                .userId(user.getUserId())
                .username(user.getUsername().getValue())
                .password(user.getPassword().getValue())
                .email(user.getEmail().getValue())
                .userType(user.getUserType())
                .isActive(user.isActive())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        if (user.getProfile() != null) {
            UserProfileEntity profileEntity = UserProfileEntity.builder()
                    .userAuth(authEntity)
                    .firstName(user.getProfile().getFirstName())
                    .lastName(user.getProfile().getLastName())
                    .phoneNumber(user.getProfile().getPhoneNumber())
                    .phoneVerified(user.getProfile().isPhoneVerified())
                    .languageCode(user.getProfile().getLanguageCode())
                    .avatarUrl(user.getProfile().getAvatarUrl())
                    .bio(user.getProfile().getBio())
                    .timezone(user.getProfile().getTimezone())
                    .dateOfBirth(user.getProfile().getDateOfBirth())
                    .build();

            authEntity = authEntity.withProfile(profileEntity);
        }

        return authEntity;
    }

    @Override
    public @NonNull User toDomain(UserAuthEntity entity) {
        Objects.requireNonNull(entity);

        Username username = Username.of(entity.getUsername());
        Email email = Email.of(entity.getEmail());
        Password password = Password.ofHashed(entity.getPassword());

        UserProfile profile = null;
        if (entity.getProfile() != null) {
            UserProfileEntity profileEntity = entity.getProfile();
            UserProfileSnapshot profileSnapshot = new UserProfileSnapshot(
                    profileEntity.getFirstName(),
                    profileEntity.getLastName(),
                    profileEntity.getPhoneNumber(),
                    profileEntity.isPhoneVerified(),
                    profileEntity.getLanguageCode(),
                    profileEntity.getAvatarUrl(),
                    profileEntity.getBio(),
                    profileEntity.getTimezone(),
                    profileEntity.getDateOfBirth());
            profile = UserProfile.of(profileSnapshot);
        }

        UserSnapshot snapshot = new UserSnapshot(
                entity.getUserId(),
                username,
                email,
                password,
                entity.getUserType(),
                entity.isActive(),
                entity.isEmailVerified(),
                profile,
                entity.getCreatedAt(),
                entity.getUpdatedAt());

        return User.reconstitute(snapshot);
    }
}
