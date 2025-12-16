package com.spacecodee.securityspacee.user.domain.repository;

import java.util.Optional;

import com.spacecodee.securityspacee.user.domain.model.User;
import com.spacecodee.securityspacee.user.domain.valueobject.Email;
import com.spacecodee.securityspacee.user.domain.valueobject.Username;

public interface IUserRepository {

    User save(User user);

    Optional<User> findById(Long userId);

    Optional<User> findByUsername(Username username);

    Optional<User> findByEmail(Email email);

    boolean existsByUsername(Username username);

    boolean existsByEmail(Email email);

    void deleteById(Long userId);
}
