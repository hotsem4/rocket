package com.rocket.domains.user.domain.repository;

import com.rocket.domains.user.domain.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

  User saveUser(User user);

  Optional<User> findByEmail(String email);

  List<User> findAll();

  Boolean deleteUser(String email);

  Boolean existsByEmail(String email);

  Boolean existsById(Long id);

  Boolean existsByNickname(String nickname);

  Optional<User> findById(Long userId);

}
